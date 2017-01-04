package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.util.Assert.isNotNull;
import static com.sforce.soap.partner.Connector.newConnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBRef;
import com.nowellpoint.api.model.domain.Backup;
import com.nowellpoint.api.model.domain.Environment;
import com.nowellpoint.api.model.domain.RunHistory;
import com.nowellpoint.api.model.domain.ScheduledJob;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
import com.nowellpoint.util.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.mongodb.document.MongoDatastore;
import com.nowellpoint.mongodb.document.MongoDocumentService;
import com.nowellpoint.util.Assert;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.api.service.ScheduledJobService;

public class SalesforceMetadataBackupJob2 implements Job {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceMetadataBackupJob2.class);
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String bucketName = "nowellpoint-metadata-backups";
	private static final Client client = new Client();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println(context.getJobDetail().getKey().getGroup());
		System.out.println(context.getJobDetail().getKey().getName());
		
		ScheduledJobService scheduledJobService = new ScheduledJobService();
		
		ScheduledJob scheduledJob = null;
		
		try {
			scheduledJob = scheduledJobService.findScheduledJobById(context.getJobDetail().getKey().getName());
		} catch (DocumentNotFoundException e) {
			LOGGER.error(e);
			return;
		}
		
		try {
			
			SalesforceConnectorService salesforceConnectorService = new SalesforceConnectorService();
			
			scheduledJob.setStatus("Running");
			scheduledJobService.updateScheduledJob(scheduledJob.getId(), scheduledJob);
			
			//
			// get environment associated with the ScheduledJob
			//
			
			Environment environment = salesforceConnectorService.getEnvironment(scheduledJob.getConnectorId(), scheduledJob.getEnvironmentKey());
			
			//
			// get User Properties
			//
			
			Map<String, UserProperty> properties = getUserProperties(environment.getKey());
			
			// 
			// authenticate to the environment
			//
			
			String accessToken = null;
			
			if ("password".equals(environment.getGrantType())) {
				accessToken = authenticate(environment.getAuthEndpoint(), environment.getUsername(), properties.get("password").getValue(), properties.get("securityToken").getValue());
			} else {
				accessToken = authenticate(properties.get("refresh.token").getValue());
			}
			
			//
			// get the identity of the user associate to the environment
			//
			
			Identity identity = getIdentity(accessToken, environment.getIdentityId());
			
			// 
			// DescribeGlobal
			//
			
			DescribeGlobalSobjectsResult describeGlobalSobjectsResult = describeGlobalSobjectsRequest(accessToken, identity.getUrls().getSobjects());
			
			//
			// create keyName
			//
			
			String keyName = String.format("%s/DescribeGlobalResult-%s", environment.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
			
			//
			// write the result to S3
			//
			
			PutObjectResult result = putObject(keyName, describeGlobalSobjectsResult);
			
			//
			// add Backup reference to ScheduledJobRequest
			//
			
			//scheduledJobRequest.addBackup(new Backup("DescribeGlobal", keyName, result.getMetadata().getContentLength()));
			
			//
			// DescribeSobjectResult - build full description first run, capture changes for each subsequent run
			//
			
			List<DescribeSobjectResult> describeSobjectResults = describeSobjects(accessToken, identity.getUrls().getSobjects(), describeGlobalSobjectsResult, scheduledJob.getLastRunDate());
			
			//
			// if describeSobjectResults is not empty then save to S3
			//
			
			if (! describeSobjectResults.isEmpty()) {
				
				//
				// create keyName
				//
				
				keyName = String.format("%s/DescribeSobjectResult-%s", environment.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
				
				//
				// write the result to S3
				//
				
				result = putObject(keyName, describeSobjectResults);
				
				//
				// add Backup reference to ScheduledJobRequest
				//
				
				//scheduledJobRequest.addBackup(new Backup("DescribeSobjects", keyName, result.getMetadata().getContentLength()));
			}
			
			//
			// add theme
			//
			
			keyName = String.format("%s/Theme-%s", environment.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
			
			//
			// get theme
			//
			
			Theme theme = getTheme(accessToken, identity.getUrls().getRest());
 
			// 
			// write result to S3
			//
			
			result = putObject(keyName, theme);
			
			//
			// add Backup reference to ScheduledJobRequest
			//
			
			//scheduledJobRequest.addBackup(new Backup("Theme", keyName, result.getMetadata().getContentLength()));
			
			// 
			// set status
			//
			
			//scheduledJobRequest.setStatus("Success");
			
			//
			// udpate environment with lastest information
			//
			
			environment.setTheme(theme);
			environment.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
			
			//
			// create and add RunHistory
			//
			
			RunHistory runHistory = new RunHistory();
			runHistory.setFireInstanceId(context.getFireInstanceId());
			//runHistory.setFireTime(fireTime);
			//runHistory.setStatus(scheduledJobRequest.getStatus());
			//runHistory.setBackups(scheduledJobRequest.getBackups());
			//runHistory.setFailureMessage(scheduledJobRequest.getFailureMessage());
			//runHistory.setJobRunTime(scheduledJobRequest.getJobRunTime());
			
			//
			// update ScheduledJob
			//
			
			if (scheduledJob.getRunHistories() == null) {
				scheduledJob.setRunHistories(new HashSet<RunHistory>());
			} else if (scheduledJob.getRunHistories().size() == 10) {
				List<RunHistory> runHistories = scheduledJob.getRunHistories().stream().sorted((r1, r2) -> r1.getFireTime().compareTo(r2.getFireTime())).collect(Collectors.toList());
				runHistories.remove(0);
				scheduledJob.setRunHistories(new HashSet<RunHistory>(runHistories));
			}
			
			scheduledJob.getRunHistories().add(runHistory);
			
			scheduledJob.setStatus("Scheduled");
			scheduledJob.setScheduleDate(Date.from(ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC")).plusDays(1).toInstant()));
			//scheduledJob.setLastRunStatus(scheduledJobRequest.getStatus());
			//scheduledJob.setLastRunFailureMessage(scheduledJobRequest.getFailureMessage());
			//scheduledJob.setLastRunDate(fireTime);
			scheduledJob.setSystemModifiedDate(Date.from(Instant.now()));		    	
			
			
			//
			// setup the next scheduled job
			//			    	
			
			//scheduledJobRequestService.create(setupNextScheduledJobRequest(scheduledJob));
		
		} catch (Exception e) {
			//scheduledJobRequest.setStatus("Failure");
			//scheduledJobRequest.setFailureMessage(e.getMessage());
		} finally {
			scheduledJobService.updateScheduledJob(scheduledJob.getId(), scheduledJob);
		}
	}
	
	/**
	 * 
	 * 
	 * @param accessToken
	 * @param identityId
	 * @return
	 * 
	 * 
	 */
	
	private Identity getIdentity(String accessToken, String identityId) {
		GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
    			.setAccessToken(accessToken)
    			.setId(identityId);
    	
    	Identity identity = client.getIdentity(getIdentityRequest);
    	
    	return identity;
	}
	
	/**
	 * 
	 * 
	 * @param email
	 * @param name
	 * @param body
	 * @throws IOException
	 * 
	 */
	
	private void sendNotification(String email, String subject, String body) throws IOException {
		Email from = new Email();
		from.setEmail("support@nowellpoint.com");
		from.setName("Nowellpoint Support");
	    
	    Email to = new Email();
	    to.setEmail(email);
	    
	    Content content = new Content();
	    content.setType("text/plain");
	    content.setValue(body);
	    
	    SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
	    
	    Personalization personalization = new Personalization();
	    personalization.addTo(to);
	    personalization.setSubject(subject);
	    
	    Mail mail = new Mail();
	    mail.setFrom(from);
	    mail.addContent(content);
	    mail.addPersonalization(personalization);
	    
	    Request request = new Request();
	    request.method = Method.POST;
    	request.endpoint = "mail/send";
    	request.body = mail.build();
    	sendgrid.api(request);
	}
	
	/**
	 * 
	 * 
	 * @param authEndpoint
	 * @param username
	 * @param password
	 * @param securityToken
	 * @return access token from Salesforce
	 * @throws JobExecutionException
	 * 
	 * 
	 */
	
	private String authenticate(String authEndpoint, String username, String password, String securityToken) throws Exception {
		
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/%s", authEndpoint, System.getProperty(Properties.SALESFORCE_API_VERSION)));
		config.setUsername(username);
		config.setPassword(password.concat(securityToken));
		
		try {
			PartnerConnection connection = newConnection(config);
			return connection.getConfig().getSessionId();
		} catch (ConnectionException e) {
			if (e instanceof LoginFault) {
				LoginFault loginFault = (LoginFault) e;
				throw new Exception(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
			} else {
				throw new Exception(e.getMessage());
			}
		}	
	}
	
	/**
	 * 
	 * 
	 * @param refreshToken
	 * @return access token from Salesforce
	 * 
	 * 
	 */
	
	private String authenticate(String refreshToken) {
		
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse authenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = authenticationResponse.getToken();
		
		return token.getAccessToken();
	}
	
	/**
	 * 
	 * 
	 * @param environmentKey
	 * @return
	 * 
	 * 
	 */
	
	private Map<String, UserProperty> getUserProperties(String environmentKey) {
		
		UserProperty userProperty = new UserProperty();
		userProperty.setSubject(environmentKey);
		
		Map<String, UserProperty> properties = UserProperties.query(userProperty)
				.stream()
				.collect(Collectors.toMap(UserProperty::getKey, p -> p));
		
		return properties;
	}
	
	/**
	 * 
	 * 
	 * @param keyName
	 * @param object
	 * @throws JsonProcessingException
	 * 
	 * 
	 */
	
	private PutObjectResult putObject(String keyName, Object object) throws JsonProcessingException {
		byte[] bytes = objectMapper.writeValueAsBytes(object);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		
		AmazonS3 s3client = new AmazonS3Client();
		
		PutObjectResult result = s3client.putObject(new PutObjectRequest(
				bucketName,
				keyName,
				new ByteArrayInputStream(bytes),
				objectMetadata));
		
		result.getMetadata().setContentLength(bytes.length);
		
		return result;
	}
	
	/**
	 * 
	 * 
	 * @param accessToken
	 * @param sobjectsUrl
	 * @return
	 * @throws JsonProcessingException
	 * 
	 * 
	 */
	
	private DescribeGlobalSobjectsResult describeGlobalSobjectsRequest(String accessToken, String sobjectsUrl) throws JsonProcessingException {

		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(accessToken)
				.setSobjectsUrl(sobjectsUrl);
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
		return describeGlobalSobjectsResult;
	}
	
	/**
	 * 
	 * 
	 * @param accessToken
	 * @param sobjectsUrl
	 * @param describeGlobalSobjectsResult
	 * @param modifiedSince
	 * @return list of DescribeSobjectResult
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws JsonProcessingException
	 * 
	 * 
	 */
	
	private List<DescribeSobjectResult> describeSobjects(String accessToken, String sobjectsUrl, DescribeGlobalSobjectsResult describeGlobalSobjectsResult, Date modifiedSince) throws InterruptedException, ExecutionException, JsonProcessingException {
		List<DescribeSobjectResult> describeResults = new ArrayList<DescribeSobjectResult>();
		List<Future<DescribeSobjectResult>> tasks = new ArrayList<Future<DescribeSobjectResult>>();
				
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
			Future<DescribeSobjectResult> task = executor.submit(() -> {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(accessToken)
						.withSobjectsUrl(sobjectsUrl)
						.withSobject(sobject.getName())
						.withIfModifiedSince(modifiedSince);

				DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

				return describeSobjectResult;
			});
			
			tasks.add(task);
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
		
		for (Future<DescribeSobjectResult> task : tasks) {
			if (Assert.isNotNull(task.get())) {
				describeResults.add(task.get());
			}
		}
		
		return describeResults;
	}
	
	/**
	 * 
	 */
	
	private Theme getTheme(String accessToken, String restEndpoint) {
		ThemeRequest themeRequest = new ThemeRequest()
				.withAccessToken(accessToken)
				.withRestEndpoint(restEndpoint);
		
		Theme theme = client.getTheme(themeRequest);
		
		return theme;
	}
}
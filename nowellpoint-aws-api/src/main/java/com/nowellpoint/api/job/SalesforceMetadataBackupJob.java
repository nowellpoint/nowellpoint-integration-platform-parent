package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
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
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.model.document.Environment;
import com.nowellpoint.api.model.document.RunHistory;
import com.nowellpoint.api.model.document.SalesforceConnector;
import com.nowellpoint.api.model.document.ScheduledJob;
import com.nowellpoint.api.model.document.ScheduledJobRequest;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.mongodb.document.MongoDocumentService;
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

public class SalesforceMetadataBackupJob implements Job {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String bucketName = "nowellpoint-metadata-backups";
	private static final Client client = new Client();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		ScheduledJobService scheduledJobService = new ScheduledJobService();
		ScheduledJobRequestService scheduledJobRequestService = new ScheduledJobRequestService();
		SalesforceConnectorService salesforceConnectorService = new SalesforceConnectorService();
		
		Set<ScheduledJobRequest> scheduledJobRequests = scheduledJobRequestService.getScheduledJobRequests();
		
		if (scheduledJobRequests.size() > 0) {
			ExecutorService executor = Executors.newFixedThreadPool(scheduledJobRequests.size());
			
			scheduledJobRequests.stream().forEach(scheduledJobRequest -> {
				
		    	executor.submit(() -> {
		    		
		    		Date fireTime = Date.from(Instant.now());
		    		
		    		ScheduledJob scheduledJob = null;
		    		
		    		String message = null;
		    		
		    		try {
		    			
		    			//
		    			// update ScheduledJobRequest
		    			//
		    			
		    			scheduledJobRequest.setStatus("Running");
		    			scheduledJobRequest.setGroupName(context.getJobDetail().getKey().getGroup());
		    			scheduledJobRequest.setJobName(context.getJobDetail().getKey().getName());
		    			scheduledJobRequest.setScheduleDate(Date.from(ZonedDateTime.ofInstant(scheduledJobRequest.getScheduleDate().toInstant(), ZoneId.of("UTC")).plusDays(1).toInstant()));
		    			scheduledJobRequestService.replace(scheduledJobRequest);
		    			
		    			//
		    			// update ScheduledJob
		    			//
		    			
		    			scheduledJob = scheduledJobService.findById(scheduledJobRequest.getScheduledJobId());
		    			scheduledJob.setStatus(scheduledJobRequest.getStatus());
		    			scheduledJobService.replace(scheduledJob);
			    	
			    		//
			    		// get environment assoicated with the ScheduledJob
			    		//
			    		
			    		Environment environment = salesforceConnectorService.getEnvironment(scheduledJobRequest.getConnectorId(), scheduledJobRequest.getEnvironmentKey());
			    		
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
				    	
				    	putObject(keyName, describeGlobalSobjectsResult);
				    	
				    	//
				    	// DescribeSobjectResult
				    	//
				    	
				    	List<DescribeSobjectResult> describeSobjectResults = describeSobjects(accessToken, identity.getUrls().getSobjects(), describeGlobalSobjectsResult);
				    	
				    	//
				    	// create keyName
				    	//
				    	
				    	keyName = String.format("%s/DescribeSobjectResult-%s", environment.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
				    	
				    	//
						// write the result to S3
						//
				    	
				    	putObject(keyName, describeSobjectResults);
				    	
				    	// 
				    	// set status
				    	//
				    	
				    	scheduledJobRequest.setStatus("Success");
				    	
				    	//
				    	//
				    	//
				    	
				    	message = String.format("Scheduled Job: %s was completed successfully at %s", scheduledJobRequest.getJobName(), Date.from(Instant.now()).toString());
				    	
			    	} catch (Exception e) {
			    		scheduledJobRequest.setStatus("Failure");
			    		scheduledJobRequest.setFailureMessage(e.getMessage());
			    		message = String.format("Scheduled Job: %s failed with exception: %s", scheduledJobRequest.getJobName(), e.getMessage());
			    	} 
		    		
		    		sendNotification(scheduledJobRequest.getNotificationEmail(), message);
		    		
		    		RunHistory runHistory = new RunHistory();
			    	runHistory.setFireInstanceId(context.getFireInstanceId());
			    	runHistory.setFireTime(fireTime);
			    	runHistory.setStatus(scheduledJobRequest.getStatus());
			    	runHistory.setFailureMessage(scheduledJobRequest.getFailureMessage());
			    	runHistory.setJobRunTime(Date.from(Instant.now()).getTime() - fireTime.getTime());
			    	scheduledJob.addRunHistory(runHistory);
			    	
			    	scheduledJob.setStatus(scheduledJobRequest.getStatus());
			    	scheduledJob.setScheduleDate(scheduledJobRequest.getScheduleDate());
			    	scheduledJob.setLastRunStatus(scheduledJobRequest.getStatus());
		    		scheduledJob.setLastRunFailureMessage(scheduledJobRequest.getFailureMessage());
			    	scheduledJob.setLastRunDate(fireTime);
			    	scheduledJob.setSystemModifiedDate(Date.from(Instant.now()));		    	
			    	scheduledJobService.replace(scheduledJob);
			    				    	
			    	ZonedDateTime dateTime = ZonedDateTime.ofInstant(scheduledJobRequest.getScheduleDate().toInstant(), ZoneId.of("UTC"));
			    	
			    	scheduledJobRequest.setJobRunTime(fireTime.getTime() - System.currentTimeMillis());
			    	scheduledJobRequest.setYear(dateTime.getYear());
			    	scheduledJobRequest.setMonth(dateTime.getMonth().getValue());
			    	scheduledJobRequest.setDay(dateTime.getDayOfMonth());
			    	scheduledJobRequest.setHour(dateTime.getHour());
			    	scheduledJobRequest.setMinute(dateTime.getMinute());
			    	scheduledJobRequest.setSecond(dateTime.getSecond());
			    	scheduledJobRequest.setStatus("Scheduled");
			    	scheduledJobRequestService.replace(scheduledJobRequest);
			    	
			    	return null;
		    	});
		    });
		    
		    executor.shutdown();
		    
		    try {
				executor.awaitTermination(30, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new JobExecutionException(e.getMessage());
			}
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
	
	private void sendNotification(String email, String body) throws IOException {
		Email from = new Email();
		from.setEmail("administrator@nowellpoint.com");
		from.setName("Nowellpoint Support");
	    
	    Email to = new Email();
	    to.setEmail(email);
	    
	    Content content = new Content();
	    content.setType("text/plain");
	    content.setValue(body);
	    
	    SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
	    
	    Personalization personalization = new Personalization();
	    personalization.addTo(to);
	    
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
	
	private void putObject(String keyName, Object object) throws JsonProcessingException {
		byte[] bytes = objectMapper.writeValueAsBytes(object);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		
		AmazonS3 s3client = new AmazonS3Client();
		
		s3client.putObject(new PutObjectRequest(
				bucketName,
				keyName,
				new ByteArrayInputStream(bytes),
				objectMetadata));
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
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws JsonProcessingException
	 * 
	 * 
	 */
	
	private List<DescribeSobjectResult> describeSobjects(String accessToken, String sobjectsUrl, DescribeGlobalSobjectsResult describeGlobalSobjectsResult) throws InterruptedException, ExecutionException, JsonProcessingException {
		
		List<DescribeSobjectResult> describeResults = new ArrayList<DescribeSobjectResult>();
		List<Future<DescribeSobjectResult>> tasks = new ArrayList<Future<DescribeSobjectResult>>();
				
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
			Future<DescribeSobjectResult> task = executor.submit(() -> {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(accessToken)
						.withSobjectsUrl(sobjectsUrl)
						.withSobject(sobject.getName());

				DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

				return describeSobjectResult;
			});
			
			tasks.add(task);
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
		
		for (Future<DescribeSobjectResult> task : tasks) {
			describeResults.add(task.get());
		}
		
		return describeResults;
	}
}

class SalesforceConnectorService extends MongoDocumentService<SalesforceConnector> {
	
	public SalesforceConnectorService() {
		super(SalesforceConnector.class);
	}
	
	public Environment getEnvironment(String id, String key) {
		SalesforceConnector salesforceConnector = super.findById(id);
		
		Environment environment = salesforceConnector.getEnvironments()
				.stream()
				.filter(e -> key.equals(e.getKey()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid environment key: " + key));
		
		return environment;
	}
}

class ScheduledJobRequestService extends MongoDocumentService<ScheduledJobRequest> {

	public ScheduledJobRequestService() {
		super(ScheduledJobRequest.class);
	}
	
	public void replace(ScheduledJobRequest scheduledJobRequest) {
		super.replace(System.getProperty(Properties.DEFAULT_SUBJECT), scheduledJobRequest);
	}
	
	public Set<ScheduledJobRequest> getScheduledJobRequests() {
		LocalDateTime  now = LocalDateTime.now(Clock.systemUTC()); 
		return super.find( and ( 
				eq ( "status", "Scheduled" ), 
				eq ( "jobTypeCode", "SALESFORCE_METADATA_BACKUP" ),
				eq ( "year", now.get( ChronoField.YEAR_OF_ERA ) ),
				eq ( "month", now.get( ChronoField.MONTH_OF_YEAR ) ),
				eq ( "day", now.get( ChronoField.DAY_OF_MONTH ) ),
				eq ( "hour", now.get( ChronoField.HOUR_OF_DAY ) ) ) );
	}
}

class ScheduledJobService extends MongoDocumentService<ScheduledJob> {

	public ScheduledJobService() {
		super(ScheduledJob.class);
	}
	
	public void replace(ScheduledJob scheduledJob) {
		hset(encode(scheduledJob.getOwner().getIdentity().getId().toString()), scheduledJob);
		super.replace( eq ( "_id", scheduledJob.getId() ), scheduledJob );
	}
	
	public ScheduledJob findById(ObjectId id) {
		return super.findOne( eq ( "_id", id ) );
	}
}
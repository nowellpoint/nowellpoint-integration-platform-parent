package com.nowellpoint.api.job;

import static com.sforce.soap.partner.Connector.newConnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.model.document.Job;
import com.nowellpoint.api.model.document.JobExecution;
import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.api.rest.domain.SalesforceConnectionString;
import com.nowellpoint.api.rest.service.VaultEntryServiceImpl;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.DigitalSignature;
import com.nowellpoint.util.Properties;
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

public class SalesforceMetadataBackupJob implements org.quartz.Job {
	
	private DocumentManagerFactory documentManagerFactory;

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String bucketName = "nowellpoint-metadata-backups";
	private static final Client client = new Client();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		System.out.println(context.getJobDetail().getKey().getName());
		
		documentManagerFactory = Datastore.getCurrentSession();
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		
		Job job = documentManager.fetch(Job.class, new ObjectId(context.getJobDetail().getKey().getName()));
		job.setStatus("Running");
		
		documentManager.replaceOne(job);
		
		if (Assert.isNull(context.getNextFireTime())) {
			job.setStatus("Complete");
		} else {
			job.setStatus("Scheduled");
		}
		
		VaultEntryService vaultEntryService = new VaultEntryServiceImpl();
		
		VaultEntry vaultEntry = vaultEntryService.retrive(job.getSource().getConnectionString());
		
		SalesforceConnectionString salesforceConnectionString = SalesforceConnectionString.of(vaultEntry.getValue());
		
		try {
			Token token = login(salesforceConnectionString);
			System.out.println(token.getAccessToken());
			
		} catch (OauthException e) {
			job.setStatus("Error");
			job.setFailureMessage(e.getError());
		} catch (ConnectionException e) {
			job.setStatus("Error");
			job.setFailureMessage(e.getMessage());
		}
		
		JobExecution jobExecution = new JobExecution();
		jobExecution.setFireInstanceId(context.getFireInstanceId());
		jobExecution.setFireTime(context.getFireTime());
		jobExecution.setJobRunTime(context.getJobRunTime());
		jobExecution.setStatus(job.getStatus());
		
		job.addJobExecution(jobExecution);
		job.setNumberOfExecutions(job.getNumberOfExecutions().intValue() + 1);
		job.setJobRunTime(context.getJobRunTime());
		job.setFireTime(context.getFireTime());
		job.setNextFireTime(context.getNextFireTime());
		
		documentManager.replaceOne(job);
		

//			    		
//			    		String accessToken = null;
//
//			    		if ("password".equals(instance.getGrantType())) {
//			    			accessToken = authenticate(instance.getAuthEndpoint(), instance.getUsername(), properties.get("password").getValue(), properties.get("securityToken").getValue());
//			    		} else {
//			    			accessToken = authenticate(properties.get("refresh.token").getValue());
//			    		}
//
//			    		//
//			    		// get the identity of the user associate to the environment
//			    		//
//			    		
//				    	Identity identity = getIdentity(accessToken, instance.getIdentityId());
//						
//				    	// 
//				    	// DescribeGlobal
//				    	//
//
//						DescribeGlobalSobjectsResult describeGlobalSobjectsResult = describeGlobalSobjectsRequest(accessToken, identity.getUrls().getSobjects());
//				    	
//						//
//						// create keyName
//						//
//
//				    	String keyName = String.format("%s/DescribeGlobalResult-%s", instance.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
//				    	
//				    	//
//						// write the result to S3
//						//
//
//				    	PutObjectResult result = putObject(keyName, describeGlobalSobjectsResult);
//				    	
//				    	//
//				    	// add Backup reference to ScheduledJobRequest
//				    	//
//				    	
//				    	scheduledJobRequest.addBackup(new Backup("DescribeGlobal", keyName, result.getMetadata().getContentLength()));
//				    	
//				    	//
//				    	// DescribeSobjectResult - build full description first run, capture changes for each subsequent run
//				    	//
//
//				    	List<DescribeSobjectResult> describeSobjectResults = describeSobjects(accessToken, identity.getUrls().getSobjects(), describeGlobalSobjectsResult, scheduledJob.getLastRunDate());
//				    	
//				    	//
//				    	// if describeSobjectResults is not empty then save to S3
//				    	//
//				    	
//				    	if (! describeSobjectResults.isEmpty()) {
//				    		
//				    		//
//					    	// create keyName
//					    	//
//					    	
//					    	keyName = String.format("%s/DescribeSobjectResult-%s", instance.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
//					    	
//					    	//
//							// write the result to S3
//							//
//
//					    	result = putObject(keyName, describeSobjectResults);
//					    	
//					    	//
//					    	// add Backup reference to ScheduledJobRequest
//					    	//
//
//					    	scheduledJobRequest.addBackup(new Backup("DescribeSobjects", keyName, result.getMetadata().getContentLength()));
//				    		
//				    	}
//				    	
//				    	//
//				    	// add theme
//				    	//
//				    	
//				    	keyName = String.format("%s/Theme-%s", instance.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
//				    	
//				    	//
//				    	// get theme
//				    	//
//
//				    	Theme theme = getTheme(accessToken, identity.getUrls().getRest());
//				    	
//				    	// 
//				    	// write result to S3
//				    	//
//
//				    	result = putObject(keyName, theme);
//				    	
//				    	//
//				    	// add Backup reference to ScheduledJobRequest
//				    	//
//
//				    	scheduledJobRequest.addBackup(new Backup("Theme", keyName, result.getMetadata().getContentLength()));
//				    	
//				    	// 
//				    	// set status
//				    	//
//				    	
//				    	scheduledJobRequest.setStatus("Success");
//				    	
//				    	//
//				    	// udpate environment with lastest information
//				    	//
//
//				    	instance.setTheme(theme);
//				    	instance.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
//
//		    		} catch (OauthException e) {
//		    			scheduledJobRequest.setStatus("Failure");
//		    			scheduledJobRequest.setFailureMessage(e.getError().concat(": ").concat(e.getErrorDescription()));
//			    	} catch (Exception e) {
//			    		scheduledJobRequest.setStatus("Failure");
//			    		scheduledJobRequest.setFailureMessage(e.getMessage());
//			    	} 
//		    		
//		    		//
//		    		// compile and send notification
//		    		//
//		    		
//		    		if (Assert.isNotNull(scheduledJobRequest.getNotificationEmail())) {
//		    			
//		    			String format = "%-20s%s%n";
//			    		
//			    		String subject = String.format("[%s] Scheduled Job Request Complete", scheduledJobRequest.getEnvironmentName());
//			    		
//			    		String message = new StringBuilder()
//			    				.append(String.format(format, "Scheduled Job:", scheduledJobRequest.getJobTypeName()))
//			    				.append(String.format(format, "Completion Date:", Date.from(Instant.now()).toString()))
//			    				.append(String.format(format, "Status:", scheduledJobRequest.getStatus()))
//			    				.append(isNotNull(scheduledJobRequest.getFailureMessage()) ? String.format(format, "Exception:", scheduledJobRequest.getFailureMessage()) : "")
//			    				.toString();
//			    		
//			    		sendNotification(scheduledJobRequest.getNotificationEmail(), subject, message);
//		    		}
//		    		
//		    		//
//		    		// update ScheduledJobRequest
//		    		//
//		    		
//		    		scheduledJobRequest.setJobRunTime(System.currentTimeMillis() - fireTime.getTime());
//			    	documentManager.replaceOne( scheduledJobRequest );
//			    	
//			    	//
//			    	// create and add RunHistory
//			    	//
//		    		
//		    		RunHistory runHistory = new RunHistory();
//			    	runHistory.setFireInstanceId(context.getFireInstanceId());
//			    	runHistory.setFireTime(fireTime);
//			    	runHistory.setStatus(scheduledJobRequest.getStatus());
//			    	runHistory.setBackups(scheduledJobRequest.getBackups());
//			    	runHistory.setFailureMessage(scheduledJobRequest.getFailureMessage());
//			    	runHistory.setJobRunTime(scheduledJobRequest.getJobRunTime());
//			    	
//			    	//
//			    	// update ScheduledJob
//			    	//
//			    	
//			    	if (scheduledJob.getRunHistories() == null) {
//						scheduledJob.setRunHistories(new HashSet<RunHistory>());
//					} else if (scheduledJob.getRunHistories().size() == 10) {
//						List<RunHistory> runHistories = scheduledJob.getRunHistories().stream().sorted((r1, r2) -> r1.getFireTime().compareTo(r2.getFireTime())).collect(Collectors.toList());
//						runHistories.remove(0);
//						scheduledJob.setRunHistories(new HashSet<RunHistory>(runHistories));
//					}
//			    	
//			    	scheduledJob.getRunHistories().add(runHistory);
//			    	
//			    	scheduledJob.setStatus("Scheduled");
//			    	scheduledJob.setScheduleDate(Date.from(ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC")).plusDays(1).toInstant()));
//			    	scheduledJob.setLastRunStatus(scheduledJobRequest.getStatus());
//		    		scheduledJob.setLastRunFailureMessage(scheduledJobRequest.getFailureMessage());
//			    	scheduledJob.setLastRunDate(fireTime);	    	
//			    	documentManager.replaceOne(scheduledJob);
//			    	
//			    	//
//			    	// setup the next scheduled job
//			    	//			    	
//			    	
//			    	documentManager.insertOne(setupNextScheduledJobRequest(scheduledJob));
//			    	
//			    	return null;
//		    	});
//		    });
//		    
//		    executor.shutdown();
//		    
//		    try {
//				executor.awaitTermination(30, TimeUnit.SECONDS);
//			} catch (InterruptedException e) {
//				throw new JobExecutionException(e.getMessage());
//			}
//		}
	}
	
	public Token login(SalesforceConnectionString salesforceConnectionString) throws ConnectionException, OauthException {
		
		Token token = null;
		
		if (SalesforceConnectionString.PASSWORD.equals(salesforceConnectionString.getGrantType())) {
			
			String[] credentials = salesforceConnectionString.getCredentials().split(":");
			
			String authEndpoint = salesforceConnectionString.getHostname();
			String username = credentials[0];
			String password = credentials[1];
			String securityToken = credentials[2];
			
			token = login(authEndpoint, username, password, securityToken);
			
			return token;
			
		} else {
			
			String refreshToken = salesforceConnectionString.getCredentials();
			
			OauthAuthenticationResponse authenticationResponse = refreshToken(refreshToken);
			
			token = authenticationResponse.getToken();
			
			return token;
		}
	}
	
	public OauthAuthenticationResponse refreshToken(String refreshToken) {
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse authenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return authenticationResponse;
	}
	
	public Token login(String authEndpoint, String username, String password, String securityToken) throws ConnectionException {
		Optional.of(authEndpoint).orElseThrow(() -> new IllegalArgumentException("missing authEndpoint"));
		Optional.of(username).orElseThrow(() -> new IllegalArgumentException("missing username")); 
		Optional.of(password).orElseThrow(() -> new IllegalArgumentException("missing password")); 
		Optional.of(securityToken).orElseThrow(() -> new IllegalArgumentException("missing securityToken")); 
		
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/%s", authEndpoint, System.getProperty(Properties.SALESFORCE_API_VERSION)));
		config.setUsername(username);
		config.setPassword(password.concat(securityToken));
		
		try {
			PartnerConnection connection = newConnection(config);
			
			String id = String.format("%s/id/%s/%s", authEndpoint, connection.getUserInfo().getOrganizationId(), connection.getUserInfo().getUserId());
			String accessToken = connection.getConfig().getSessionId();
			String instanceUrl = connection.getConfig().getServiceEndpoint().substring(0, connection.getConfig().getServiceEndpoint().indexOf("/services"));
			String issuedAt = String.valueOf(connection.getServerTimestamp().getTimestamp().getTimeInMillis());
			String signature = DigitalSignature.sign(System.getenv("SALESFORCE_CLIENT_SECRET"), id.concat(issuedAt));
			
			Token token = new Token();
			token.setId(id);
			token.setAccessToken(accessToken);
			token.setInstanceUrl(instanceUrl);
			token.setIssuedAt(issuedAt);
			token.setTokenType("Bearer");
			token.setSignature(signature);
			
			return token;
			
		} catch (ConnectionException e) {
			if (e instanceof LoginFault) {
				LoginFault loginFault = (LoginFault) e;
				throw new ConnectionException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
			} else {
				throw e;
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

//class SalesforceConnectorService {
//	
//	private MongoDocumentService mongoDocumentService = new MongoDocumentService();
//	
//	public Instance getEnvironment(String id, String key) {
//		
//		SalesforceConnector salesforceConnector = mongoDocumentService.findOne(SalesforceConnector.class, eq ( "_id", new ObjectId( id ) ) );
//		
//		Instance instance = salesforceConnector.getInstances()
//				.stream()
//				.filter(e -> key.equals(e.getKey()))
//				.findFirst()
//				.orElseThrow(() -> new IllegalArgumentException("Invalid environment key: " + key));
//		
//		return instance;
//	}
//}

//class ScheduledJobRequestService extends AbstractCacheService {
//	
//	private MongoDocumentService mongoDocumentService = new MongoDocumentService();
//	
//	public void create(ScheduledJobRequest scheduledJobRequest) {
//		mongoDocumentService.create(scheduledJobRequest);
//		hset(System.getProperty(Properties.DEFAULT_SUBJECT), scheduledJobRequest);
//	}
//	
//	public void replace(ScheduledJobRequest scheduledJobRequest) {
//		mongoDocumentService.replace(scheduledJobRequest);
//		hset(System.getProperty(Properties.DEFAULT_SUBJECT), scheduledJobRequest);
//	}
//	
//	public Set<ScheduledJobRequest> getScheduledJobRequests() {
//		LocalDateTime  now = LocalDateTime.now(Clock.systemUTC()); 
//		FindIterable<ScheduledJobRequest> documents = mongoDocumentService.find(ScheduledJobRequest.class, and ( 
//				eq ( "status", "Scheduled" ), 
//				eq ( "jobTypeCode", "SALESFORCE_METADATA_BACKUP" ),
//				eq ( "year", now.get( ChronoField.YEAR_OF_ERA ) ),
//				eq ( "month", now.get( ChronoField.MONTH_OF_YEAR ) ),
//				eq ( "day", now.get( ChronoField.DAY_OF_MONTH ) ),
//				eq ( "hour", now.get( ChronoField.HOUR_OF_DAY ) ) ) );
//		
//		Set<ScheduledJobRequest> scheduledJobs = new HashSet<ScheduledJobRequest>();
//		
//		documents.forEach(new Block<ScheduledJobRequest>() {
//			@Override
//			public void apply(final ScheduledJobRequest document) {
//				scheduledJobs.add(document);
//		    }
//		});
//		
//		return scheduledJobs;
//		
//	}
//}

//class ScheduledJobService extends AbstractCacheService {
//	
//	private MongoDocumentService mongoDocumentService = new MongoDocumentService();
//	
//	public void replace(ScheduledJob scheduledJob) {
//		hset(scheduledJob.getOwner().getIdentity().getId().toString(), scheduledJob);
//		mongoDocumentService.replace( scheduledJob );
//	}
//	
//	public ScheduledJob findById(ObjectId id) {
//		return mongoDocumentService.findOne(ScheduledJob.class, eq ( "_id", id ) );
//	}
//}
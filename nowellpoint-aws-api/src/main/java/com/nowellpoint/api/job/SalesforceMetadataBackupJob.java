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
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBRef;
import com.nowellpoint.api.model.document.Backup;
import com.nowellpoint.api.model.document.Environment;
import com.nowellpoint.api.model.document.RunHistory;
import com.nowellpoint.api.model.document.SalesforceConnector;
import com.nowellpoint.api.model.document.ScheduledJob;
import com.nowellpoint.api.model.document.ScheduledJobRequest;
import com.nowellpoint.api.model.document.UserRef;
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
import com.nowellpoint.mongodb.document.MongoDatastore;
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
		    		
		    		try {
		    			
		    			//
		    			// update ScheduledJobRequest
		    			//
		    			
		    			scheduledJobRequest.setStatus("Running");
		    			scheduledJobRequest.setFireInstanceId(context.getFireInstanceId());
		    			scheduledJobRequest.setGroupName(context.getJobDetail().getKey().getGroup());
		    			scheduledJobRequest.setJobName(context.getJobDetail().getKey().getName());
		    			scheduledJobRequestService.replace( scheduledJobRequest );
		    			
		    			//
		    			// update ScheduledJob
		    			//
		    			
		    			scheduledJob = scheduledJobService.findById(scheduledJobRequest.getScheduledJobId());
		    			scheduledJob.setStatus(scheduledJobRequest.getStatus());
		    			scheduledJobService.replace(scheduledJob);
			    	
			    		//
			    		// get environment associated with the ScheduledJob
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
				    	
				    	PutObjectResult result = putObject(keyName, describeGlobalSobjectsResult);
				    	
				    	//
				    	// add Backup reference to ScheduledJobRequest
				    	//
				    	
				    	scheduledJobRequest.addBackup(new Backup("DescribeGlobal", keyName, result.getMetadata().getContentLength()));
				    	
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
				    	
				    	result = putObject(keyName, describeSobjectResults);
				    	
				    	//
				    	// add Backup reference to ScheduledJobRequest
				    	//
				    	
				    	scheduledJobRequest.addBackup(new Backup("DescribeSobjects", keyName, result.getMetadata().getContentLength()));
				    	
				    	// 
				    	// set status
				    	//
				    	
				    	scheduledJobRequest.setStatus("Success");
				    	
			    	} catch (Exception e) {
			    		scheduledJobRequest.setStatus("Failure");
			    		scheduledJobRequest.setFailureMessage(e.getMessage());
			    	} 
		    		
		    		//
		    		// compile and send notification
		    		//
		    		
		    		String message = new StringBuilder().append("Scheduled Job: ")
		    				.append(scheduledJobRequest.getJobName())
		    				.append(System.getProperty("line.separator"))
		    				.append("Date: ")
		    				.append(Date.from(Instant.now()))
		    				.append(System.getProperty("line.separator"))
		    				.append("Status: ")
		    				.append(scheduledJobRequest.getStatus())
		    				.append(System.getProperty("line.separator"))
		    				.append("Exception: " )
		    				.append(isNotNull(scheduledJobRequest.getFailureMessage()) ? scheduledJobRequest.getFailureMessage() : "")
		    				.toString();
		    		
		    		sendNotification(scheduledJobRequest.getNotificationEmail(), "Scheduled Job Request Complete", message);
		    		
		    		//
		    		// update ScheduledJobRequest
		    		//
		    		
		    		scheduledJobRequest.setJobRunTime(System.currentTimeMillis() - fireTime.getTime());
			    	scheduledJobRequestService.replace( scheduledJobRequest );
			    	
			    	//
			    	// create and add RunHistory
			    	//
		    		
		    		RunHistory runHistory = new RunHistory();
			    	runHistory.setFireInstanceId(context.getFireInstanceId());
			    	runHistory.setFireTime(fireTime);
			    	runHistory.setStatus(scheduledJobRequest.getStatus());
			    	runHistory.setBackups(scheduledJobRequest.getBackups());
			    	runHistory.setFailureMessage(scheduledJobRequest.getFailureMessage());
			    	runHistory.setJobRunTime(scheduledJobRequest.getJobRunTime());
			    	scheduledJob.addRunHistory(runHistory);
			    	
			    	//
			    	// update ScheduledJob
			    	//
			    	
			    	scheduledJob.setStatus("Scheduled");
			    	scheduledJob.setScheduleDate(Date.from(ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC")).plusDays(1).toInstant()));
			    	scheduledJob.setLastRunStatus(scheduledJobRequest.getStatus());
		    		scheduledJob.setLastRunFailureMessage(scheduledJobRequest.getFailureMessage());
			    	scheduledJob.setLastRunDate(fireTime);
			    	scheduledJob.setSystemModifiedDate(Date.from(Instant.now()));		    	
			    	scheduledJobService.replace(scheduledJob);
			    	
			    	//
			    	// setup the next scheduled job
			    	//			    	
			    	
//			    	scheduledJobRequest.setId(new ObjectId());
//			    	scheduledJobRequest.setFireInstanceId(null);
//			    	scheduledJobRequest.setJobRunTime(null);
//			    	scheduledJobRequest.setBackups(null);
//			    	scheduledJobRequest.setCreatedDate(Date.from(Instant.now()));
//			    	scheduledJobRequest.setLastModifiedDate(Date.from(Instant.now()));
//			    	scheduledJobRequest.setScheduleDate(scheduledJob.getScheduleDate());
//			    	scheduledJobRequest.setYear(dateTime.getYear());
//			    	scheduledJobRequest.setMonth(dateTime.getMonth().getValue());
//			    	scheduledJobRequest.setDay(dateTime.getDayOfMonth());
//			    	scheduledJobRequest.setHour(dateTime.getHour());
//			    	scheduledJobRequest.setMinute(dateTime.getMinute());
//			    	scheduledJobRequest.setSecond(dateTime.getSecond());
//			    	scheduledJobRequest.setStatus("Scheduled");
			    	scheduledJobRequestService.create(setupNextScheduledJobRequest(scheduledJob));
			    	
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
	
	private ScheduledJobRequest setupNextScheduledJobRequest(ScheduledJob scheduledJob) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC"));
		
		String collectionName = MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class );
		ObjectId id = new ObjectId( System.getProperty( Properties.DEFAULT_SUBJECT ) );
    	
    	DBRef reference = new DBRef( collectionName, id );
		
		UserRef userRef = new UserRef();
		userRef.setIdentity(reference);
    	
		ScheduledJobRequest scheduledJobRequest = new ScheduledJobRequest();
		scheduledJobRequest.setScheduledJobId(scheduledJob.getId());
		scheduledJobRequest.setConnectorId(scheduledJob.getConnectorId());
		scheduledJobRequest.setConnectorType(scheduledJob.getConnectorType());
		scheduledJobRequest.setOwner(scheduledJob.getOwner());
		scheduledJobRequest.setCreatedDate(Date.from(Instant.now()));
		scheduledJobRequest.setCreatedBy(userRef);
		scheduledJobRequest.setDescription(scheduledJob.getDescription());
		scheduledJobRequest.setEnvironmentKey(scheduledJob.getEnvironmentKey());
		scheduledJobRequest.setEnvironmentName(scheduledJob.getEnvironmentName());
		scheduledJobRequest.setIsSandbox(scheduledJob.getIsSandbox());
		scheduledJobRequest.setJobTypeCode(scheduledJob.getJobTypeCode());
		scheduledJobRequest.setJobTypeId(scheduledJob.getJobTypeCode());
		scheduledJobRequest.setJobTypeName(scheduledJob.getJobTypeName());
		scheduledJobRequest.setStatus(scheduledJob.getStatus());
		scheduledJobRequest.setLastModifiedDate(Date.from(Instant.now()));
		scheduledJobRequest.setLastModifiedBy(userRef);
		scheduledJobRequest.setNotificationEmail(scheduledJob.getNotificationEmail());
		scheduledJobRequest.setScheduleDate(scheduledJob.getScheduleDate());
		scheduledJobRequest.setSystemCreationDate(Date.from(Instant.now()));
		scheduledJobRequest.setSystemModifiedDate(Date.from(Instant.now()));
		scheduledJobRequest.setYear(dateTime.getYear());
		scheduledJobRequest.setMonth(dateTime.getMonth().getValue());
		scheduledJobRequest.setDay(dateTime.getDayOfMonth());
		scheduledJobRequest.setHour(dateTime.getHour());
		scheduledJobRequest.setMinute(dateTime.getMinute());
		scheduledJobRequest.setSecond(dateTime.getSecond());
		
		return scheduledJobRequest;
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
	
	public void create(ScheduledJobRequest scheduledJobRequest) {
		super.create(scheduledJobRequest);
		hset(encode(System.getProperty(Properties.DEFAULT_SUBJECT)), scheduledJobRequest);
	}
	
	public void replace(ScheduledJobRequest scheduledJobRequest) {
		super.replace(scheduledJobRequest);
		hset(encode(System.getProperty(Properties.DEFAULT_SUBJECT)), scheduledJobRequest);
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
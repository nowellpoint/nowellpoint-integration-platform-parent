package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.sforce.soap.partner.Connector.newConnection;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import com.nowellpoint.api.model.document.SalesforceConnector;
import com.nowellpoint.api.model.document.ScheduledJob;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.mongodb.document.MongoDocumentService;
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
		SalesforceConnectorService salesforceConnectorService = new SalesforceConnectorService();
		
		Set<ScheduledJob> scheduledJobs = scheduledJobService.getScheduledJobs();
		
		if (scheduledJobs.size() > 0) {
			ExecutorService executor = Executors.newFixedThreadPool(scheduledJobs.size());
			
		    scheduledJobs.stream().forEach(scheduledJob -> {
		    	
		    	executor.submit(() -> {
		    		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		    		
		    		scheduledJob.setStatus("Running");
		    		scheduledJob.setScheduleDate(Date.from(ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC")).plusDays(1).toInstant()));
			    	
			    	scheduledJobService.replace(scheduledJob);
			    	
			    	try {
			    		
			    		Environment environment = salesforceConnectorService.getEnvironment(scheduledJob.getConnectorId(), scheduledJob.getEnvironmentKey());
			    	
			    		String accessToken = authenticate(environment);
				    	
				    	GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
				    			.setAccessToken(accessToken)
				    			.setId(environment.getIdentityId());
				    	
				    	Identity identity = client.getIdentity(getIdentityRequest);
				    	
				    	describeGlobalSobjectsRequest(accessToken, environment.getOrganizationId(), identity.getUrls().getSobjects());
				    	
				    	scheduledJob.setLastRunStatus("Success");
				    	
			    	} catch (Exception e) {
			    		scheduledJob.setLastRunStatus("Failure");
			    		scheduledJob.setFailureMessage(e.getMessage());
			    	}
			    				    	
			    	ZonedDateTime dateTime = ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC"));
			    	scheduledJob.setYear(dateTime.getYear());
			    	scheduledJob.setMonth(dateTime.getMonth().getValue());
			    	scheduledJob.setDay(dateTime.getDayOfMonth());
			    	scheduledJob.setHour(dateTime.getHour());
			    	scheduledJob.setMinute(dateTime.getMinute());
			    	scheduledJob.setSecond(dateTime.getSecond());
			    	scheduledJob.setStatus("Scheduled");
			    	scheduledJob.setLastRunDate(Date.from(Instant.now()));
			    	
			    	scheduledJobService.replace(scheduledJob);
			    	
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
	
	private String authenticate(Environment environment) throws JobExecutionException {
		UserProperty userProperty = new UserProperty();
		userProperty.setSubject(environment.getKey());
		
		Map<String, UserProperty> properties = UserProperties.query(userProperty)
				.stream()
				.collect(Collectors.toMap(UserProperty::getKey, p -> p));

		if ("password".equals(environment.getGrantType())) {
			
			String authEndpoint = environment.getAuthEndpoint();
			String username = environment.getUsername();
			String password = properties.get("password").getValue();
			String securityToken = properties.get("securityToken").getValue();
			
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
					throw new JobExecutionException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
				} else {
					throw new JobExecutionException(e.getMessage());
				}
			}	
			
		} else {
			
			String refreshToken = properties.get("refresh.token").getValue();

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
	}
	
	private DescribeGlobalSobjectsResult describeGlobalSobjectsRequest(String accessToken, String organizationId, String sobjectsUrl) throws JsonProcessingException {
		AmazonS3 s3client = new AmazonS3Client();
		
		String keyName = String.format("%s/DescribeGlobalResult-%s", organizationId, dateFormat.format(Date.from(Instant.now())));
		
		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(accessToken)
				.setSobjectsUrl(sobjectsUrl);
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
		byte[] bytes = objectMapper.writeValueAsBytes(describeGlobalSobjectsResult);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		
		s3client.putObject(new PutObjectRequest(
				bucketName,
				keyName,
				new ByteArrayInputStream(bytes),
				objectMetadata));
		
		return describeGlobalSobjectsResult;
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

class ScheduledJobService extends MongoDocumentService<ScheduledJob> {

	public ScheduledJobService() {
		super(ScheduledJob.class);
	}
	
	public ScheduledJob replace(ScheduledJob scheduledJob) {
		return super.replace(scheduledJob.getOwner().getIdentity().getId().toString(), scheduledJob);
	}
	
	public Set<ScheduledJob> getScheduledJobs() {
		LocalDateTime  now = LocalDateTime.now(Clock.systemUTC()); 
		return super.find( and ( 
				eq ( "status", "Scheduled" ), 
				eq ( "jobTypeCode", "SALESFORCE_METADATA_BACKUP" ),
				eq ( "year", now.get( ChronoField.YEAR_OF_ERA ) ),
				eq ( "month", now.get( ChronoField.MONTH_OF_YEAR ) ),
				eq ( "day", now.get( ChronoField.DAY_OF_MONTH ) ) ) );
	}
}
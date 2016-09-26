package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.sforce.soap.partner.Connector.newConnection;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.ws.rs.InternalServerErrorException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.model.document.Environment;
import com.nowellpoint.api.model.document.ScheduledJob;
import com.nowellpoint.api.model.dynamodb.UserProperties;
import com.nowellpoint.api.model.dynamodb.UserProperty;
import com.nowellpoint.api.service.ServiceException;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.LoginResult;
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
	private static Client client = new Client();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		ScheduledJobService service = new ScheduledJobService();
		
		Set<ScheduledJob> scheduledJobs = service.getScheduledJobs();
		
		if (scheduledJobs.size() > 0) {
			ExecutorService executor = Executors.newFixedThreadPool(scheduledJobs.size());
			
		    scheduledJobs.stream().forEach(scheduledJob -> {
		    	
		    	executor.submit(() -> {
		    		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		    		
		    		scheduledJob.setStatus("Running");
		    		scheduledJob.setScheduleDate(Date.from(ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC")).plusDays(1).toInstant()));
			    	scheduledJob.setLastRunStatus("Success");
			    	scheduledJob.setLastRunDate(Date.from(Instant.now()));
			    	
			    	service.replace(scheduledJob);
			    	
			    	ZonedDateTime dateTime = ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC"));
			    	scheduledJob.setYear(dateTime.getYear());
			    	scheduledJob.setMonth(dateTime.getMonth().getValue());
			    	scheduledJob.setDay(dateTime.getDayOfMonth());
			    	scheduledJob.setHour(dateTime.getHour());
			    	scheduledJob.setMinute(dateTime.getMinute());
			    	scheduledJob.setSecond(dateTime.getSecond());
			    	scheduledJob.setStatus("Scheduled");
			    	
			    	service.replace(scheduledJob);
		    	});
		    });
		}
	}
	
	private void authenticate(Environment environment) {
		UserProperty userProperty = new UserProperty();
		userProperty.setSubject(environment.getKey());
		
		Map<String, UserProperty> properties = UserProperties.query(userProperty)
				.stream()
				.collect(Collectors.toMap(UserProperty::getKey, p -> p));

		if (environment.getIsSandbox()) {
			
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
			} catch (ConnectionException e) {
				if (e instanceof LoginFault) {
					LoginFault loginFault = (LoginFault) e;
					throw new ServiceException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
				} else {
					throw new InternalServerErrorException(e.getMessage());
				}
			}	
			
		} else {
			
			String refreshToken = properties.get("refreshToken").getValue();

			RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
					.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
					.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
					.setRefreshToken(refreshToken)
					.build();
			
			OauthAuthenticationResponse authenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			Token token = authenticationResponse.getToken();
			
			System.out.println(token.getAccessToken());
		}	
	}
	
	private DescribeGlobalSobjectsResult describeGlobalSobjectsRequest(Environment environment) throws JsonProcessingException {
		AmazonS3 s3client = new AmazonS3Client();
		
		String keyName = String.format("%s/DescribeGlobalResult-%s", environment.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
		
		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken("")
				.setSobjectsUrl("");
		
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

class ScheduledJobService extends MongoDocumentService<ScheduledJob> {

	public ScheduledJobService() {
		super(ScheduledJob.class);
	}
	
	public ScheduledJob replace(ScheduledJob scheduledJob) {
		return super.replace(scheduledJob.getOwner().getIdentity().getId().toString(), scheduledJob);
	}
	
	public Set<ScheduledJob> getScheduledJobs() {
		return super.find( and ( 
				eq ( "status", "Scheduled" ), 
				eq ( "jobTypeCode", "SALESFORCE_METADATA_BACKUP" ) ) );
	}
}
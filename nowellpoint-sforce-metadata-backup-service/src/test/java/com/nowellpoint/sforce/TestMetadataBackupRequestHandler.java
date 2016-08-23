package com.nowellpoint.sforce;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.sforce.model.MetadataBackupRequest;

public class TestMetadataBackupRequestHandler {

	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	@BeforeClass
	public static void init() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}
	
	@Test
	public void testSubmitMetatdataBackupRequest() {
		
		UsernamePasswordGrantRequest request = OauthRequests.USERNAME_PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.USERNAME_PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			MetadataBackupRequest metadataBackupRequest = new MetadataBackupRequest();
			metadataBackupRequest.setOrganizationId(response.getIdentity().getOrganizationId());
			metadataBackupRequest.setSobjectsUrl(response.getIdentity().getUrls().getSobjects());
			metadataBackupRequest.setSessionId(response.getToken().getAccessToken());
			metadataBackupRequest.setAwsAccessKey(System.getenv("AWS_ACCESS_KEY"));
			metadataBackupRequest.setAwsSecretAccessKey(System.getenv("AWS_SECRET_ACCESS_KEY"));
			metadataBackupRequest.setBucketName("nowellpoint-metadata-backups");
			
			dynamoDBMapper.save(metadataBackupRequest);
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		}	
	}	
}
package com.nowellpoint.client.auth;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

public class TestAuthenticators {
	
	@BeforeClass
	public static void before() {
		System.setProperty("javax.net.ssl.trustStore", "/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home/jre/lib/security/keystore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "secret");
	}
	
	@Test
	public void testClientCredentialsGrantAuthentication() {
		
		try {	
			
			long start = System.currentTimeMillis();
			
			ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
					.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
					.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
					.build();
			
			OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			Token token = response.getToken();
			
			System.out.println("testClientCredentialsGrantAuthentication: " + (System.currentTimeMillis() - start));
			
			assertNotNull(token);
			assertNotNull(token.getExpiresIn());
			assertNotNull(token.getTokenType());
			
			System.out.println(token.getAccessToken());
			
			RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
					.setAccessToken(token.getAccessToken())
					.build();
			
			Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
			
			System.out.println("testClientCredentialsGrantAuthentication: " + (System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			
			GetResult<AccountProfile> getResult = new NowellpointClient(new TokenCredentials(token))
					.accountProfile()
					.get();
			
			System.out.println("testClientCredentialsGrantAuthentication: " + (System.currentTimeMillis() - start));
			
			System.out.println(getResult.getTarget().getName());
			
			List<SalesforceConnector> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
					.salesforceConnector()
					.getSalesforceConnectors();
			
			System.out.println(salesforceConnectors.get(0).getId());
			System.out.println(salesforceConnectors.get(0).getEnvironments().get(0).getKey());
			
			start = System.currentTimeMillis();
			
			UpdateResult<Environment> updateResult = new NowellpointClient(new TokenCredentials(token))
					.salesforceConnector()
					.environment()
					.build(salesforceConnectors.get(0).getId(), salesforceConnectors.get(0).getEnvironments().get(0).getKey());
			
			System.out.println(updateResult.isSuccess());
			System.out.println(updateResult.getErrorMessage());
			System.out.println("buildEnvironment: " + (System.currentTimeMillis() - start));
			
			Environment environment = updateResult.getTarget();
			
			System.out.println(environment.getKey());
			
		} catch (OauthException e) {
			System.out.println(e.getCode());
			System.out.println(e.getMessage());
		} 
	}
	
	@Test
	@Ignore
	public void testPasswordGrantAuthentication() {
		
		try {			
			
			long start = System.currentTimeMillis();
			
			PasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
					.setUsername(System.getenv("NOWELLPOINT_USERNAME"))
					.setPassword(System.getenv("NOWELLPOINT_PASSWORD"))
					.build();
		
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
						.authenticate(request);
			
			Token token = response.getToken();
			
			System.out.println("testPasswordGrantAuthentication: " + (System.currentTimeMillis() - start));
			
			assertNotNull(token);
			assertNotNull(token.getExpiresIn());
			assertNotNull(token.getRefreshToken());
			assertNotNull(token.getTokenType());
			
			System.out.println(token.getAccessToken());
			
			RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
					.setAccessToken(token.getAccessToken())
					.build();
			
			Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
			
			System.out.println("testPasswordGrantAuthentication: " + (System.currentTimeMillis() - start));
			
//			NowellpointClient client = new NowellpointClient(new EnvironmentVariablesCredentials());
//			
//			CreateScheduledJobRequest createScheduledJobRequest = new CreateScheduledJobRequest()
//					.withConnectorId("57df3e22019362745462305c")
//					.withDescription("My scheduled job description")
//					.withJobTypeId("57d7e6ccb55f01245754d0af")
//					.withScheduleDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
//			
//			ScheduledJob scheduledJob = client.scheduledJob()
//					.create(createScheduledJobRequest)
//					.getScheduledJob();
//			
//			assertNotNull(scheduledJob);
//			assertNotNull(scheduledJob.getId());
//			
//			System.out.println(scheduledJob.getId());
//			
//			assertEquals(scheduledJob.getEnvironmentKey(), "40799dbbe97b479baa0772eb4e6ba8cb");
//			
//			GetResult<ScheduledJob> scheduledJobResult = client.scheduledJob().get(scheduledJob.getId());
//			
//			assertNotNull(scheduledJobResult.getTarget());
//			
//			UpdateScheduledJobRequest updateScheduledJobRequest = new UpdateScheduledJobRequest()
//					.withId(scheduledJob.getId())
//					.withConnectorId("57df3e22019362745462305c")
//					.withDescription("My scheduled job description with update")
//					.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse("2016-10-01".concat("T").concat("16:00:00")));
//			
//			scheduledJob = client.scheduledJob()
//					.update(updateScheduledJobRequest)
//					.getScheduledJob();
//			
//			assertNotNull(scheduledJob);
//			assertEquals(scheduledJob.getDescription(), "My scheduled job description with update");
//			
//			System.out.println(scheduledJob.getId());
//			
//			DeleteResult deleteResult = client.scheduledJob().delete(scheduledJob.getId());
//			
//			assertEquals(deleteResult.getIsSuccess(), Boolean.TRUE);
//			
//			client.logout();
			
		} catch (OauthException e) {
			System.out.println(e.getCode());
			System.out.println(e.getMessage());
		} catch (NowellpointServiceException e) {
			System.out.println(e.getMessage());
		} 
	}
}
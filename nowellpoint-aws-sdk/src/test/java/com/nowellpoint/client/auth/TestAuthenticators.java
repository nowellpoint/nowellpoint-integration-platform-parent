package com.nowellpoint.client.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.EnvironmentRequest;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

public class TestAuthenticators {
	
	private static Logger LOG = Logger.getLogger(TestAuthenticators.class.getName());
	private static Token token = null;
	
	@BeforeClass
	public static void before() {
		System.setProperty("javax.net.ssl.trustStore", "/Library/Java/JavaVirtualMachines/jdk1.8.0_111.jdk/Contents/Home/jre/lib/security/keystore.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "secret");
		
		long start = System.currentTimeMillis();
		
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		token = response.getToken();
		
		System.out.println("testClientCredentialsGrantAuthentication: " + (System.currentTimeMillis() - start));
		
		assertNotNull(token);
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getTokenType());
		
		System.out.println(token.getAccessToken());
	}
	
	@Test
	@Ignore
	public void testUpdateSalesforceConnector() {
		String name = UUID.randomUUID().toString();
		
		SalesforceConnectorRequest salesforceConnectorRequest = new SalesforceConnectorRequest()
				.withName(name);
		
		GetResult<List<SalesforceConnector>> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.getSalesforceConnectors();
		
		SalesforceConnector salesforceConnector = salesforceConnectors.getTarget().get(0);
		
		UpdateResult<SalesforceConnector> updateResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.update(salesforceConnector.getId(), salesforceConnectorRequest);
		
		assertTrue(updateResult.isSuccess());
		
		GetResult<SalesforceConnector> getSalesforceConnector = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.get(salesforceConnector.getId());
		
		assertEquals(getSalesforceConnector.getTarget().getName(), name);
		
		System.out.println(getSalesforceConnector.getTarget().getName());
	}
	
	@Test
	public void createAndDeleteEnvironment() {
		
		LOG.info("start createAndDeleteEnvironment");
		
		GetResult<SalesforceConnector> getResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.get("5838ae0d25075c7a81115253");
		
		SalesforceConnector salesforceConnector = getResult.getTarget();
		
		String authEndpoint = "https://login.salesforce.com";
		String environmentName = "Test Environment";
		String password = System.getenv("SALESFORCE_PASSWORD");
		String username = System.getenv("SALESFORCE_USERNAME");
		String securityToken = System.getenv("SALESFORCE_SECURITY_TOKEN");
		
		EnvironmentRequest environmentRequest = new EnvironmentRequest()
				.withIsActive(Boolean.TRUE)
				.withAuthEndpoint(authEndpoint)
				.withEnvironmentName(environmentName)
				.withPassword(password)
				.withUsername(username)
				.withSecurityToken(securityToken);
		
		CreateResult<Environment> createResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.add(salesforceConnector.getId(), environmentRequest);
		
		System.out.println("Create Result: " + createResult.isSuccess());
		System.out.println(createResult.getErrorMessage());
		
		String environmentKey = createResult.getTarget().getKey();
		
		System.out.println(environmentKey);
		
		UpdateResult<Environment> testConnection = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.test(salesforceConnector.getId(), environmentKey);
		
		System.out.println("Test Result: " + testConnection.isSuccess());
		System.out.println(testConnection.getErrorMessage());
		
		UpdateResult<Environment> buildResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.build(salesforceConnector.getId(), environmentKey);
		
		System.out.println("Build Result: " + buildResult.isSuccess());
		System.out.println(buildResult.getErrorMessage());
		
		DeleteResult deleteResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.delete(salesforceConnector.getId(), createResult.getTarget().getKey());
		
		System.out.println("Delete Result: " + deleteResult.isSuccess());		
	}
	
	@Test
	@Ignore
	public void testGetAccountProfile() {
		
		long start = System.currentTimeMillis();
		
		GetResult<AccountProfile> getResult = new NowellpointClient(new TokenCredentials(token))
				.accountProfile()
				.get();
		
		System.out.println("testGetAccountProfile : " + (System.currentTimeMillis() - start));
		
		System.out.println(getResult.getTarget().getName());
	}
	
	@Test
	@Ignore
	public void buildEnvironment() {
		long start = System.currentTimeMillis();
		
		GetResult<List<SalesforceConnector>> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.getSalesforceConnectors();
		
		SalesforceConnector salesforceConnector = salesforceConnectors.getTarget().get(0);
		
		UpdateResult<Environment> updateResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.build(salesforceConnector.getId(), salesforceConnector.getEnvironments().get(0).getKey());
		
		System.out.println(updateResult.isSuccess());
		System.out.println(updateResult.getErrorMessage());
		System.out.println("buildEnvironment: " + (System.currentTimeMillis() - start));
		
		Environment environment = updateResult.getTarget();
		
		System.out.println(environment.getKey());
	}
	
	@Test
	@Ignore
	public void testConnection() {
		long start = System.currentTimeMillis();
		
		GetResult<List<SalesforceConnector>> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.getSalesforceConnectors();
		
		SalesforceConnector salesforceConnector = salesforceConnectors.getTarget().get(0);
		
		UpdateResult<Environment> updateResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.test(salesforceConnector.getId(), salesforceConnector.getEnvironments().get(0).getKey());
		
		System.out.println(updateResult.isSuccess());
		System.out.println(updateResult.getErrorMessage());
		System.out.println("testConnection: " + (System.currentTimeMillis() - start));
		
		Environment environment = updateResult.getTarget();
		
		System.out.println(environment.getKey());
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
	
	@AfterClass
	public static void afterClass() {
		long start = System.currentTimeMillis();
		
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setAccessToken(token.getAccessToken())
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
		
		System.out.println("revoke token: " + (System.currentTimeMillis() - start));
	}
}
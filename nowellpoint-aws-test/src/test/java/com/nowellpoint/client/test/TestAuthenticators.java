package com.nowellpoint.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.EnvironmentRequest;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Instance;
import com.nowellpoint.client.model.SObjectDetail;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.SalesforceConnectorRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

public class TestAuthenticators {
	
	private static Logger LOG = Logger.getLogger(TestAuthenticators.class.getName());
	private static Token token = null;
	
	@BeforeClass
	public static void before() {
		
		long start = System.currentTimeMillis();
		
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.setEnvironment(Environment.SANDBOX)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		token = response.getToken();
		
		System.out.println("testClientCredentialsGrantAuthentication: " + (System.currentTimeMillis() - start));
		
		assertNotNull(token);
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getTokenType());
		
		System.out.println(token.getAccessToken());
		
		start = System.currentTimeMillis();
		
		Identity identity = new NowellpointClient(token)
				.identity()
				.get(token.getId());
		
		assertNotNull(identity);	
		
		System.out.println("testGetAccountProfile : " + (System.currentTimeMillis() - start));
		
		System.out.println(identity.getName());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentException() {
		OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder().build();
	}
	
	@Test
	@Ignore
	public void testUpdateSalesforceConnector() {
		String name = UUID.randomUUID().toString();
		
		SalesforceConnectorRequest salesforceConnectorRequest = new SalesforceConnectorRequest()
				.withName(name);
		
		SalesforceConnectorList salesforceConnectors = new NowellpointClient(token)
				.salesforceConnector()
				.getSalesforceConnectors();
		
		SalesforceConnector salesforceConnector = salesforceConnectors.getItems().get(0);
		
		UpdateResult<SalesforceConnector> updateResult = new NowellpointClient(token)
				.salesforceConnector()
				.update(salesforceConnector.getId(), salesforceConnectorRequest);
		
		assertTrue(updateResult.isSuccess());
		
		salesforceConnector = new NowellpointClient(token)
				.salesforceConnector()
				.get(salesforceConnector.getId());
		
		assertEquals(salesforceConnector.getName(), name);
		
		System.out.println(salesforceConnector.getName());
	}
	
	@Test
	@Ignore
	public void createAndDeleteEnvironment() {
		
		LOG.info("start createAndDeleteEnvironment");
		
		SalesforceConnector salesforceConnector = new NowellpointClient(token)
				.salesforceConnector()
				.get("5838ae0d25075c7a81115253");
		
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
		
		CreateResult<Instance> createResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.add(salesforceConnector.getId(), environmentRequest);
		
		assertTrue(createResult.isSuccess());
		
		String environmentKey = createResult.getTarget().getKey();
		
		UpdateResult<Instance> testConnection = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.test(salesforceConnector.getId(), environmentKey);
		
		assertTrue(testConnection.isSuccess());
		
		UpdateResult<Instance> buildResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.build(salesforceConnector.getId(), environmentKey);
		
		assertTrue(buildResult.isSuccess());
		
		SObjectDetail sobjectDetail = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.sobjectDetail()
				.get(salesforceConnector.getId(), environmentKey, "Opportunity");
		
		assertNotNull(sobjectDetail);
		
		DeleteResult deleteResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.delete(salesforceConnector.getId(), createResult.getTarget().getKey());
		
		assertTrue(deleteResult.isSuccess());		
	}
	
	@Test
	public void testGetIdentity() {
		
		long start = System.currentTimeMillis();
		
		Identity identity = new NowellpointClient(token)
				.identity()
				.get(token.getId());
		
		assertNotNull(identity);	
		
		System.out.println("testGetAccountProfile : " + (System.currentTimeMillis() - start));
		
		System.out.println(identity.getName());
	}
	
	@Test
	@Ignore
	public void buildEnvironment() {
		long start = System.currentTimeMillis();
		
		SalesforceConnectorList salesforceConnectors = new NowellpointClient(token)
				.salesforceConnector()
				.getSalesforceConnectors();
		
		SalesforceConnector salesforceConnector = salesforceConnectors.getItems()
				.stream()
				.filter(s -> s.getInstances() != null || s.getInstances().size() > 0)
				.findFirst()
				.get();
		
		UpdateResult<Instance> updateResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.build(salesforceConnector.getId(), salesforceConnector.getInstances().get(0).getKey());
		
		System.out.println(updateResult.isSuccess());
		System.out.println(updateResult.getErrorMessage());
		System.out.println("buildEnvironment: " + (System.currentTimeMillis() - start));
		
		Instance instance = updateResult.getTarget();
		
		System.out.println(instance.getKey());
	}
	
	@Test
	@Ignore
	public void testConnection() {
		long start = System.currentTimeMillis();
		
		SalesforceConnectorList salesforceConnectors = new NowellpointClient(token)
				.salesforceConnector()
				.getSalesforceConnectors();
		
		SalesforceConnector salesforceConnector = salesforceConnectors.getItems().get(0);
		
		UpdateResult<Instance> updateResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.test(salesforceConnector.getId(), salesforceConnector.getInstances().get(0).getKey());
		
		System.out.println(updateResult.isSuccess());
		System.out.println(updateResult.getErrorMessage());
		System.out.println("testConnection: " + (System.currentTimeMillis() - start));
		
		Instance instance = updateResult.getTarget();
		
		System.out.println(instance.getKey());
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
					.setToken(token)
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
		}
	}
	
	@AfterClass
	public static void afterClass() {
		
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(token)
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
}
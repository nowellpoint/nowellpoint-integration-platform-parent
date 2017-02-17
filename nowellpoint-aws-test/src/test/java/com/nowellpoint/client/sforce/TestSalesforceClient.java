package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.util.Properties;

public class TestSalesforceClient {
	
	@BeforeClass
	public static void before() {
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
	}
	
	@Test
	public void testSalesforceClient() {
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			assertNotNull(response.getToken());
			
			Token token = response.getToken();
			
			GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setId(response.getToken().getId());
			
			Client client = new Client();
			
			Identity identity = client.getIdentity(getIdentityRequest);
			
			assertNotNull(identity);
			
			JsonNode opportunity = new ObjectMapper().createObjectNode()
					.put("StageName", "Value Proposition");
			
			HttpResponse httpResponse = RestResource.post(identity.getUrls().getRest())
					.contentType(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
	    			.path("sobjects")
	    			.path("Opportunity")
	    			.path("00630000002XCF6AAO?_HttpMethod=PATCH")
	    			.body(opportunity)
	    			.execute();
			
			assertEquals(Integer.valueOf(Status.NO_CONTENT), Integer.valueOf(httpResponse.getStatusCode()));
				
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		}
	}
}
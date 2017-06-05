package com.nowellpoint.client.sforce;

import java.util.List;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.util.Properties;

public class TestCreate {
	
	private static Logger LOG = Logger.getLogger(TestCreate.class.getName());
	
	@BeforeClass
	public static void init() {
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
	}
	
	@Test
	public void testCreateLead() {
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			
			LOG.info("********* Singing into Salesforce...");
			
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			Token token = response.getToken();
			
			LOG.info("********* Id: " + token.getId());
			LOG.info("********* Instance Url: " + token.getInstanceUrl());
			LOG.info("********* Token Type: " + token.getTokenType());
			LOG.info("********* Access Token: " + token.getAccessToken());
			
			Identity identity = response.getIdentity();
			
			LOG.info("********* Display Name: " + identity.getDisplayName());
			LOG.info("********* REST Endpoint: " + identity.getUrls().getRest());
			
			ObjectMapper mapper = new ObjectMapper();
			
			ObjectNode lead = mapper.createObjectNode()
					.put("FirstName", "John")
					.put("LastName", "Herson")
					.put("Email", "my.test@mailinator.com ")
					.put("Phone", "000-000-0000")
					.put("Company", "My Company")
					.put("Description", "Need some help");
			
			System.out.println(lead.toString());
			
			HttpResponse httpResponse = RestResource.post(identity.getUrls().getSobjects())
					.contentType(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
	    			.path("Lead")
	    			.body(lead)
	    			.execute();
			
			LOG.info("********* HTTP Response code: " + httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == Status.CREATED) {
				CreateResult createResult = httpResponse.getEntity(CreateResult.class);
				LOG.info("********* Create Result status: " + createResult.getSuccess());
				LOG.info("********* Lead Id: " + createResult.getId());
			} else {
				List<Error> errors = httpResponse.getEntityList(Error.class);
				System.out.println(errors.get(0).getErrorCode());
				System.out.println(errors.get(0).getMessage());
			}
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (ClientException e) {
			System.out.println(e.getErrorDescription());
			System.out.println(e.getError());
			System.out.println(e.getStatusCode());
		}
	}
}
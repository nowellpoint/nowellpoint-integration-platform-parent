package com.nowellpoint.client.sforce;

import java.util.List;

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
	
	@BeforeClass
	public static void init() {
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
	}
	
	@Test
	public void testCreateContact() {
		
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
			
			Token token = response.getToken();
			
			Identity identity = response.getIdentity();
			
			ObjectMapper mapper = new ObjectMapper();
			
			ObjectNode contact = mapper.createObjectNode()
					.put("Id", "dkhdlkhfdkhjfd")
					.put("FirstName", "John")
					.put("LastName", "Herson")
					.put("Email", "test.nowellpoint@mailinator.com ")
					.put("Phone", "919-000-0000")
					.put("Company", "Nowellpoint")
					.put("Description", "Need some help");
			
			System.out.println(contact.toString());
			
			HttpResponse httpResponse = RestResource.post(identity.getUrls().getSobjects())
					.contentType(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
	    			.path("Lead")
	    			.body(contact)
	    			.execute();
			
			System.out.println(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == Status.OK) {
				CreateResult createResult = httpResponse.getEntity(CreateResult.class);
				System.out.println(createResult.getSuccess());
				System.out.println(createResult.getId());
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
package com.nowellpoint.api.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.api.dto.idp.Account;
import com.nowellpoint.api.dto.idp.Token;
import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Identity;

public class TestRestApi {
	
	@BeforeClass
	public static void init() {
		Properties.setSystemProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
	}

	//@Test
	public void testAuthentication() {
		
		System.out.println("testAuthentication");
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.accept(MediaType.APPLICATION_JSON)
					.path("oauth/token")
					.basicAuthorization(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"))
					.execute();

			assertTrue(httpResponse.getStatusCode() == Status.OK);
			
			Token token = httpResponse.getEntity(Token.class);
			
			assertNotNull(token.getAccessToken());
			assertNotNull(token.getExpiresIn());
			assertNotNull(token.getRefreshToken());
			assertNotNull(token.getTokenType());
			
			httpResponse = RestResource.get(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.bearerAuthorization(token.getAccessToken())
					.path("account")
					.path("me")
					.execute();
			
			assertTrue(httpResponse.getStatusCode() == Status.OK);
			
			Account account = httpResponse.getEntity(Account.class);
			
			assertNotNull(account);
			assertNotNull(account.getEmail());
			assertNotNull(account.getFullName());
			assertNotNull(account.getGivenName());
			assertNotNull(account.getHref());
			assertNotNull(account.getId());
			assertNotNull(account.getStatus());
			assertNotNull(account.getSurname());
			assertNotNull(account.getUsername());
			assertEquals(account.getEmail(), "john.d.herson@gmail.com");
			
			httpResponse = RestResource.get(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.bearerAuthorization(token.getAccessToken())
					.accept(MediaType.APPLICATION_JSON)
					.path("properties")
					.execute();
			
			assertTrue(httpResponse.getStatusCode() == Status.OK);
			
			httpResponse = RestResource.delete(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.bearerAuthorization(token.getAccessToken())
					.path("oauth")
					.path("token")
					.execute();
			
			assertTrue(httpResponse.getStatusCode() == Status.NO_CONTENT);
			
		} catch (HttpRequestException e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testAccount() {
		
		System.out.println("testAccount");
		
		Account account = new Account();
		account.setEmail("allbuyer@aim.com");
		account.setSurname("Buyer");
		account.setGivenName("All");
		account.setUsername("allbuyer@aim.com");
		try {
			account.setPassword(URLEncoder.encode("!t2U1&JUTJvY", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.accept(MediaType.APPLICATION_JSON)
					.path("oauth/token")
					.basicAuthorization(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"))
					.execute();
			
			assertEquals(httpResponse.getStatusCode(), 200);
			
			Token token = httpResponse.getEntity(Token.class);
			
			httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("account")
					.body(account)
					.execute();
			
			assertEquals(httpResponse.getStatusCode(), 201);
			assertNotNull(httpResponse.getHeaders().get("Location"));
			
			account = httpResponse.getEntity(Account.class);
			
			assertNotNull(account.getEmail());
			assertNotNull(account.getFullName());
			assertNotNull(account.getGivenName());
			assertNotNull(account.getHref());
			assertNotNull(account.getId());
			assertNotNull(account.getStatus());
			assertNotNull(account.getSurname());
			assertNotNull(account.getUsername());
			
			httpResponse = RestResource.delete(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.contentType(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("account")
					.path(account.getId())
					.execute();
			
			assertEquals(httpResponse.getStatusCode(), 204);
			
			httpResponse = RestResource.delete(account.getHref())
					.basicAuthorization(System.getProperty(Properties.STORMPATH_API_KEY_ID), System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
					.execute();
			
			assertEquals(httpResponse.getStatusCode(), 204);
			
		} catch (HttpRequestException e) {
			e.printStackTrace();
		}		
	}
	
	//@Test
	public void testContact() {
		
		System.out.println("testContact");
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("contact")
					.parameter("leadSource", "Contact")
					.parameter("firstName", "All")
					.parameter("lastName", "Buyer")
					.parameter("email", "allbuyer@aim.com")
					.parameter("phone", "999-999-9919")
					.parameter("company", URLEncoder.encode("Company Name", "UTF-8"))
					.parameter("description", "Just need help")
					.execute();
			
			assertEquals(httpResponse.getStatusCode(), 200);
			
			JsonNode node = httpResponse.getEntity(JsonNode.class);
			
			assertNotNull(node.get("leadId"));
			
			UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
					.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
					.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
					.setUsername(System.getenv("SALESFORCE_USERNAME"))
					.setPassword(System.getenv("SALESFORCE_PASSWORD"))
					.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
					.build();
			
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
				
			assertNotNull(response.getToken());
			
			GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setId(response.getToken().getId());
			
			Client client = new Client();
			
			Identity identity = client.getIdentity(getIdentityRequest);
				
			assertNotNull(identity);
			
			httpResponse = RestResource.delete(identity.getUrls().getRest())
				.contentType(MediaType.APPLICATION_JSON)
				.bearerAuthorization(response.getToken().getAccessToken())
	    		.path("sobjects")
	    		.path("Lead")
	    		.path(node.get("leadId").asText())
	    		.execute();
			
			assertEquals(Integer.valueOf(Status.NO_CONTENT), Integer.valueOf(httpResponse.getStatusCode()));
			    
		} catch (HttpRequestException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}	
	
	//@Test
	public void testSignUp() {
		
		System.out.println("testSignUp");
		
		String password = null;
		try {
			password = URLEncoder.encode("!t2U1&JUTJvY", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("signup")
					.parameter("leadSource", "Sign Up")
					.parameter("firstName", "Sandra")
					.parameter("lastName", "Smith")
					.parameter("email", "jherson@aim.com")
					.parameter("countryCode", "US")
					.parameter("password", password)
					.execute();
		
		assertEquals(httpResponse.getStatusCode(), 200);
		
		ObjectNode node = httpResponse.getEntity(ObjectNode.class);
		
		System.out.println(node.toString());
		
		httpResponse = RestResource.post(node.get("emailVerificationToken").asText())
				.execute();
		
		System.out.println(httpResponse.getAsString());
		
		httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("signup")
				.parameter("leadSource", "Sign Up")
				.parameter("firstName", "Sandra")
				.parameter("lastName", "Smith")
				.parameter("email", "jherson@aim.com")
				.parameter("countryCode", "US")
				.parameter("password", password)
				.execute();
		
		assertEquals(httpResponse.getStatusCode(), 409);
		
		System.out.println(httpResponse.getAsString());
					
	}	
	
	@Test
	public void testInvalidEmailVerificationToken() {
		
		System.out.println("testInvalidEmailVerificationToken");
		
		String password = null;
		try {
			password = URLEncoder.encode("!t2U1&JUTJvY", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("signup")
					.parameter("leadSource", "Sign Up")
					.parameter("firstName", "Sandra")
					.parameter("lastName", "Smith")
					.parameter("email", "jherson@aim.com")
					.parameter("countryCode", "US")
					.parameter("password", password)
					.execute();
		
		assertEquals(httpResponse.getStatusCode(), 200);
		
		ObjectNode node = httpResponse.getEntity(ObjectNode.class);
		
		System.out.println(node.toString());
		
		httpResponse = RestResource.post(node.get("emailVerificationToken").asText().concat("1"))
				.execute();
		
		System.out.println(httpResponse.getAsString());
	}
}
package com.nowellpoint.aws.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;

public class TestRestApi {
	
	private static final String NCS_API_ENDPOINT = "http://localhost:9090/rest";

	//@Test
	public void testAuthentication() {
		
		System.out.println("testAuthentication");
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(NCS_API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.path("oauth/token")
					.basicAuthorization(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"))
					.execute();
			

			assertTrue(httpResponse.getStatusCode() == Status.OK);
			
			if (httpResponse.getStatusCode() == 400) {
				System.out.println(httpResponse.getAsString());
			} else {
				Token token = httpResponse.getEntity(Token.class);
				
				System.out.println(token.getAccessToken());
				
				assertNotNull(token.getAccessToken());
				
				httpResponse = RestResource.get(NCS_API_ENDPOINT)
						.bearerAuthorization(token.getAccessToken())
						.path("account")
						.path("me")
						.execute();
				
				assertTrue(httpResponse.getStatusCode() == Status.OK);
				
				Account account = httpResponse.getEntity(Account.class);
				
				assertNotNull(account);
				assertEquals(account.getEmail(), "john.d.herson@gmail.com");
				
				httpResponse = RestResource.delete(NCS_API_ENDPOINT)
						.bearerAuthorization(token.getAccessToken())
						.path("oauth")
						.path("token")
						.execute();
				
				assertTrue(httpResponse.getStatusCode() == Status.NO_CONTENT);
				
				System.out.println(httpResponse.getStatusCode());
				System.out.println(httpResponse.getAsString());
			}
			
		} catch (HttpRequestException e) {
			e.printStackTrace();
		}
	}
	
	@Test
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
			httpResponse = RestResource.post(NCS_API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.path("oauth/token")
					.basicAuthorization(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"))
					.execute();
			
			System.out.println("authenticate: " + httpResponse.getStatusCode());
			
			Token token = null;
			
			if (httpResponse.getStatusCode() == 400) {
				System.out.println(httpResponse.getAsString());
			} else {
				token = httpResponse.getEntity(Token.class);
			}
			
			httpResponse = RestResource.post(NCS_API_ENDPOINT)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("account")
					.body(account)
					.execute();
			
			System.out.println("create: " + httpResponse.getStatusCode());
			
			account = httpResponse.getEntity(Account.class);
			
			System.out.println(httpResponse.getHeaders().get("Location"));
			
			httpResponse = RestResource.delete(NCS_API_ENDPOINT)
					.contentType(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("account")
					.path(account.getId())
					.execute();
			
			System.out.println("disable: " + httpResponse.getStatusCode());
			System.out.println(httpResponse.getAsString());
			
		} catch (HttpRequestException e) {
			e.printStackTrace();
		}		
	}
	
	//@Test
	public void testContact() {
		
		System.out.println("testContact");
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(NCS_API_ENDPOINT)
					.path("contact")
					.parameter("leadSource", "Contact")
					.parameter("firstName", "All")
					.parameter("lastName", "Buyer")
					.parameter("email", "allbuyer@aim.com")
					.parameter("phone", "999-999-9919")
					.parameter("company", URLEncoder.encode("Company Name", "UTF-8"))
					.parameter("description", "Just need help")
					.execute();
			
			System.out.println(httpResponse.getStatusCode());
			System.out.println(httpResponse.getAsString());
			
		} catch (HttpRequestException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}	
	
	//@Test
	public void testSignUp() {
		
		System.out.println("testSignUp");
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(NCS_API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.path("signup")
					.parameter("leadSource", "Sign Up")
					.parameter("firstName", "Sandra")
					.parameter("lastName", "Smith")
					.parameter("email", "jherson@aim.com")
					.parameter("countryCode", "US")
					.parameter("password", URLEncoder.encode("!t2U1&JUTJvY", "UTF-8"))
					.execute();
			
			System.out.println(httpResponse.getStatusCode());
			System.out.println(httpResponse.getAsString());
			
		} catch (HttpRequestException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}	
}
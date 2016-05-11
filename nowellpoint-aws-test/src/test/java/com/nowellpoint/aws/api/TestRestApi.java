package com.nowellpoint.aws.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Test;

import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;

public class TestRestApi {
	
	private static final String NCS_API_ENDPOINT = "http://localhost:9090/rest";

	@Test
	public void testAuthentication() {
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(NCS_API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.path("oauth/token")
					.basicAuthorization(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"))
					.execute();
			
			System.out.println(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 400) {
				System.out.println(httpResponse.getAsString());
			} else {
			
			Token token = httpResponse.getEntity(Token.class);
			
			System.out.println(token.getAccessToken());
			
			httpResponse = RestResource.delete(NCS_API_ENDPOINT)
					.bearerAuthorization(token.getAccessToken())
	    			.path("oauth")
	    			.path("token")
	    			.execute();
	    	
	    	int statusCode = httpResponse.getStatusCode();
	    	
	    	System.out.println(statusCode);
	    	System.out.println(httpResponse.getAsString());
			}
			
			
			
		} catch (HttpRequestException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testContact() {
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(NCS_API_ENDPOINT)
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
			
			System.out.println(httpResponse.getStatusCode());
			System.out.println(httpResponse.getAsString());
			
		} catch (HttpRequestException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
	}	
	
	@Test
	public void testSignUp() {
		
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
package com.nowellpoint.aws.api;

import org.junit.Test;

import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.aws.model.admin.Properties;

public class TestRestApi {
	
	//private static final String NCS_API_ENDPOINT = "https://api.nowellpoint.com/rest/v1";
	private static final String NCS_API_ENDPOINT = "http://localhost:9090/rest";

	@Test
	public void testAuthentication() {
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(NCS_API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.path("oauth/token")
					//.basicAuthorization(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"))
					.basicAuthorization("john.d.herson@gmail.com", "password")
					.execute();
			
			System.out.println(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 400) {
				System.out.println(httpResponse.getAsString());
			} else {
			
			Token token = httpResponse.getEntity(Token.class);
			
			System.out.println(token.getAccessToken());
			
			httpResponse = RestResource.delete(NCS_API_ENDPOINT)
					//.header("x-api-key", API_KEY)
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
}
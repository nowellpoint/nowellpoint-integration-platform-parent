package com.nowellpoint.aws.api;

import java.io.IOException;

import org.junit.Test;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;

public class TestRestApi {

	@Test
	public void testAuthentication() {
		//"https://api.nowellpoint.com/rest/v1"
		//"http://localhost:9090/rest"
		
		try {
			HttpResponse httpResponse = RestResource.post("https://api.nowellpoint.com/rest/v1")
					.accept(MediaType.APPLICATION_JSON)
					.path("oauth/token")
					.basicAuthorization(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"))
					.execute();
			
			System.out.println(httpResponse.getStatusCode());
			System.out.println(httpResponse.getEntity());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
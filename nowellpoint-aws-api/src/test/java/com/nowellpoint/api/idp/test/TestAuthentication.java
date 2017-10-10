package com.nowellpoint.api.idp.test;

import org.junit.Test;

import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

public class TestAuthentication {
	
	@Test
	public void testAuthentication() {
		
		HttpResponse httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Cache-Control", "no-cache")
				.path("v1")
				.path("token")
				.parameter("grant_type", "password")
				.parameter("username", System.getenv("NOWELLPOINT_USERNAME"))
				.parameter("password", System.getenv("NOWELLPOINT_PASSWORD"))
				.parameter("scope", "offline_access")
				.execute();
		
		System.out.println(httpResponse.getStatusCode());
		System.out.println(httpResponse.getAsString());
		
	}
}
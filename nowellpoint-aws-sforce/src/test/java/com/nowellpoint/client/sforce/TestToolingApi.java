package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexClassResult;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

public class TestToolingApi {
	
	@Test
	public void testToolingApi() {
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getenv("SALESFORCE_CLIENT_ID"))
				.setClientSecret(System.getenv("SALESFORCE_CLIENT_SECRET"))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse authResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			System.out.println(authResponse.getToken().getId());
			System.out.println(authResponse.getToken().getInstanceUrl());
			
			HttpResponse httpResponse = RestResource.get(authResponse.getIdentity().getUrls().getRest())
					.path("tooling")
					.accept(MediaType.APPLICATION_JSON)
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(authResponse.getToken().getAccessToken())
					.execute();
			
			getApexManifest(authResponse.getToken());
			getApexClasses(authResponse.getToken());
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} 	
	}
	
	private void getApexClasses(Token token) {
		HttpResponse httpResponse = RestResource.get(token.getInstanceUrl())
				.path("services")
				.path("data")
				.path("v42.0")
				.path("query")
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(StandardCharsets.UTF_8)
				.bearerAuthorization(token.getAccessToken())
				.queryParameter("q", "select id, body from apexclass")
				.execute();
		
		ApexClassResult result = httpResponse.getEntity(ApexClassResult.class);
		
		result.getRecords().stream().forEach(apexClass -> {
			System.out.println(apexClass.getBody());
		});
	}
	
	private void getApexManifest(Token token) {
		HttpResponse httpResponse = RestResource.get(token.getInstanceUrl())
				.path("services")
				.path("data")
				.path("v42.0")
				.path("tooling")
				.path("apexManifest")
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(StandardCharsets.UTF_8)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		System.out.println(httpResponse.getAsString());
	}
}
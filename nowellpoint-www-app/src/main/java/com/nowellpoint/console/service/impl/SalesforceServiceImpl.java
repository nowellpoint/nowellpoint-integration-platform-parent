package com.nowellpoint.console.service.impl;

import java.nio.charset.StandardCharsets;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalResult;
import com.nowellpoint.console.service.SalesforceService;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.console.util.SecretsManager;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class SalesforceServiceImpl implements SalesforceService {
	
	@Override
	public Token getToken(String authorizationCode) {
		HttpResponse response = RestResource.get(EnvironmentVariables.getSalesforceTokenUri())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
                .queryParameter("grant_type", "authorization_code")
                .queryParameter("code", authorizationCode)
                .queryParameter("client_id", SecretsManager.getSalesforceClientId())
                .queryParameter("client_secret", SecretsManager.getSalesforceClientSecret())
                .queryParameter("redirect_uri", EnvironmentVariables.getSalesforceRedirectUri())
                .execute();
        
        return response.getEntity(Token.class);
	}
	
	@Override
	public Token refreshToken(String refreshToken) {
		HttpResponse response = RestResource.get(EnvironmentVariables.getSalesforceTokenUri())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
                .queryParameter("grant_type", "refresh_token")
                .queryParameter("refresh_token", refreshToken)
                .queryParameter("client_id", SecretsManager.getSalesforceClientId())
                .queryParameter("client_secret", SecretsManager.getSalesforceClientSecret())
                .execute();
		
		Token token = null;
		
		if (response.getStatusCode() == Status.OK) {
			token = response.getEntity(Token.class);
		} else {
			
		}
		
		return token;
	}
	
	@Override
	public Identity getIdentity(Token token) {
		HttpResponse identityResponse = RestResource.get(token.getId())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.queryParameter("version", "latest")
				.execute();
		
		return identityResponse.getEntity(Identity.class);
	}
	
	@Override
	public Organization getOrganization(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getSobjects())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
     			.path("Organization")
     			.path(identity.getOrganizationId())
     			.queryParameter("fields", "Id,Name")
     			.queryParameter("version", "latest")
     			.execute();
		
		return response.getEntity(Organization.class);
	}
	
	@Override
	public DescribeGlobalResult describeGlobal(Token token) {
		
		Identity identity = getIdentity(token);
		
		HttpResponse response = RestResource.get(identity.getUrls().getSobjects())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.execute();
		
		return response.getEntity(DescribeGlobalResult.class);
	}
}
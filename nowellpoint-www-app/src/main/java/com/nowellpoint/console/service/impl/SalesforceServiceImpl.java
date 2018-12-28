package com.nowellpoint.console.service.impl;

import java.util.Set;

import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexTrigger;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Limits;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Profile;
import com.nowellpoint.client.sforce.model.RecordType;
import com.nowellpoint.client.sforce.model.Resources;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.UserRole;
import com.nowellpoint.console.service.SalesforceService;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.console.util.SecretsManager;

public class SalesforceServiceImpl implements SalesforceService {
	
	private static final Salesforce client = SalesforceClientBuilder.builder()
			.build()
			.getClient();
	
	@Override
	public Token getToken(String authorizationCode) {
		AuthorizationGrantRequest request = OauthRequests.AUTHORIZATION_GRANT_REQUEST.builder()
				.setCallbackUri(EnvironmentVariables.getSalesforceRedirectUri())
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setCode(authorizationCode)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.AUTHORIZATION_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return response.getToken();
	}
	
	@Override
	public Token refreshToken(String refreshToken) {
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return response.getToken();
	}
	
	@Override
	public Identity getIdentity(Token token) {
		return client.getIdentity(token);
	}
	
	@Override
	public Organization getOrganization(Token token) {
		return client.getOrganization(token);
	}
	
	@Override
	public DescribeGlobalResult describeGlobal(Token token) {
		return client.describeGlobal(token);
	}
	
	@Override
	public Set<UserLicense> getUserLicenses(Token token) {
		return client.getUserLicenses(token);
	}
	
	@Override
	public Set<ApexClass> getApexClasses(Token token) {
		return client.getApexClasses(token);
	}
	
	@Override
	public Set<ApexTrigger> getApexTriggers(Token token) {
		return client.getApexTriggers(token);
	}
	
	@Override
	public Set<RecordType> getRecordTypes(Token token) {
		return client.getRecordTypes(token);
	}
	
	@Override
	public Set<UserRole> getUserRoles(Token token) {
		return client.getUserRoles(token);
	}
	
	@Override
	public Set<Profile> getProfiles(Token token) {
		return client.getProfiles(token);
	}
	
	@Override
	public Resources getResources(Token token) {
		return client.getResources(token);
	}
	
	@Override
	public Limits getLimits(Token token) {
		return client.getLimits(token);
	}
}
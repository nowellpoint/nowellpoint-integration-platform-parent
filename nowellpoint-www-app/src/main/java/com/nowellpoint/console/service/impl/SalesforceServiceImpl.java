package com.nowellpoint.console.service.impl;

import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
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
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.SalesforceService;
import com.nowellpoint.util.Properties;
import com.nowellpoint.util.SecretsManager;
import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

public class SalesforceServiceImpl extends AbstractService implements SalesforceService {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceServiceImpl.class.getName());
	
	@Override
	public Token getToken(String authorizationCode) {
		AuthorizationGrantRequest request = OauthRequests.AUTHORIZATION_GRANT_REQUEST.builder()
				.setCallbackUri(System.getProperty(Properties.SALESFORCE_OAUTH_CALLBACK))
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
				.setRefreshToken(decryptToken(refreshToken))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return response.getToken();
	}
	
	@Override
	public Identity getIdentity(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getIdentity();
	}
	
	@Override
	public Organization getOrganization(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getOrganization();
	}
	
	@Override
	public DescribeGlobalResult describeGlobal(Token token) {
		return SalesforceClientBuilder.defaultClient(token).describeGlobal();
	}
	
	@Override
	public Set<UserLicense> getUserLicenses(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getUserLicenses();
	}
	
	@Override
	public Set<ApexClass> getApexClasses(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getApexClasses();
	}
	
	@Override
	public Set<ApexTrigger> getApexTriggers(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getApexTriggers();
	}
	
	@Override
	public Set<RecordType> getRecordTypes(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getRecordTypes();
	}
	
	@Override
	public Set<UserRole> getUserRoles(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getUserRoles();
	}
	
	@Override
	public Set<Profile> getProfiles(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getProfiles();
	}
	
	@Override
	public Resources getResources(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getResources();
	}
	
	@Override
	public Limits getLimits(Token token) {
		return SalesforceClientBuilder.defaultClient(token).getLimits();
	}
	
	private String decryptToken(String refreshToken) {
		try {
			return SecureValue.decryptBase64( refreshToken );
		} catch (SecureValueException e) {
			LOGGER.severe(ExceptionUtils.getStackTrace(e));
			return null;
		}
	}
}
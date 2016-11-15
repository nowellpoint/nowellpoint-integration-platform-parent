package com.nowellpoint.api.service;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.ValidationException;

import com.nowellpoint.api.model.domain.Environment;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;

public class EnvironmentService {
	
	@Inject
	private SalesforceService salesforceService;
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * @param environment
	 * @param parameters
	 * 
	 * 
	 **************************************************************************************************************************/
	
	public void testConnection(Environment environment) {
		
		try {
			
			if (environment.getIsSandbox()) {
				
				String authEndpoint = environment.getAuthEndpoint();
				String username = environment.getUsername();
				String password = environment.getPassword();
				String securityToken = environment.getSecurityToken();
				
				LoginResult loginResult = salesforceService.login(authEndpoint, username, password, securityToken);		
				environment.setUserId(loginResult.getUserId());
				environment.setOrganizationId(loginResult.getOrganizationId());
				environment.setOrganizationName(loginResult.getOrganizationName());
				environment.setServiceEndpoint(loginResult.getServiceEndpoint());
				
				Client client = new Client();
				
				GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
						.setAccessToken(loginResult.getSessionId())
						.setId(loginResult.getId());
				
				Identity identity = client.getIdentity(getIdentityRequest);
				
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(loginResult.getSessionId())
						.setSobjectsUrl(identity.getUrls().getSobjects());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				environment.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
				
				ThemeRequest themeRequest = new ThemeRequest()
						.withAccessToken(loginResult.getSessionId())
						.withRestEndpoint(identity.getUrls().getRest());
				
				Theme theme = client.getTheme(themeRequest);
				
				environment.setTheme(theme);
				
			} else {
				
				String refreshToken = environment.getRefreshToken();
				
				OauthAuthenticationResponse authenticationResponse = salesforceService.refreshToken(refreshToken);
				
				Token token = authenticationResponse.getToken();
				Identity identity = authenticationResponse.getIdentity();
				
				Client client = new Client();

				GetOrganizationRequest getOrganizationRequest = new GetOrganizationRequest()
						.setAccessToken(token.getAccessToken())
						.setOrganizationId(identity.getOrganizationId())
						.setSobjectUrl(identity.getUrls().getSobjects());
				
				Organization organization = client.getOrganization(getOrganizationRequest);
				
				environment.setUserId(identity.getUserId());
				environment.setOrganizationId(identity.getOrganizationId());
				environment.setOrganizationName(organization.getName());
				environment.setServiceEndpoint(token.getInstanceUrl());
				environment.setIsValid(Boolean.TRUE);
				
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(token.getAccessToken())
						.setSobjectsUrl(identity.getUrls().getSobjects());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				environment.setSobjects(describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet()));
				
				ThemeRequest themeRequest = new ThemeRequest()
						.withAccessToken(token.getAccessToken())
						.withRestEndpoint(identity.getUrls().getRest());
				
				Theme theme = client.getTheme(themeRequest);
				
				environment.setTheme(theme);
				
			}
			environment.setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getErrorDescription());
		} catch (ValidationException e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		} catch (Exception e) {
			environment.setIsValid(Boolean.FALSE);
			environment.setTestMessage(e.getMessage());
		}		
	}
}
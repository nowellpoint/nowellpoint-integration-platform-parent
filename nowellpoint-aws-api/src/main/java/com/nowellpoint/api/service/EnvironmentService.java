package com.nowellpoint.api.service;

import javax.inject.Inject;
import javax.validation.ValidationException;

import com.nowellpoint.api.model.domain.Environment;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.GetOrganizationRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;

public class EnvironmentService {
	
	@Inject
	private SalesforceService salesforceService;
	
	/**
	 * 
	 * @param environment
	 */
	
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
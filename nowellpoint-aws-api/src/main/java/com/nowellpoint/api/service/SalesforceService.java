package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.SalesforceConnectionString;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.sforce.ws.ConnectionException;

public interface SalesforceService {
	
	public Token login(String authEndpoint, String username, String password, String securityToken) throws ConnectionException;
	
	public Token login(SalesforceConnectionString salesforceConnectionString) throws ConnectionException, OauthException;
	
	public DescribeGlobalSobjectsResult describe(String id);
	
	public OauthAuthenticationResponse authenticate(String code);
	
	public User getUser(String accessToken, String userId, String sobjectUrl);
	
	public Organization getOrganization(String accessToken, String organizationId, String sobjectUrl);
	
	public OauthAuthenticationResponse refreshToken(String refreshToken);
}
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
	
	Token login(String authEndpoint, String username, String password, String securityToken) throws ConnectionException;
	
	Token login(SalesforceConnectionString salesforceConnectionString) throws ConnectionException, OauthException;
	
	DescribeGlobalSobjectsResult describe(String id);
	
	OauthAuthenticationResponse authenticate(String code);
	
	User getUser(String accessToken, String userId, String sobjectUrl);
	
	Organization getOrganization(String accessToken, String organizationId, String sobjectUrl);
	
	OauthAuthenticationResponse refreshToken(String refreshToken);
}
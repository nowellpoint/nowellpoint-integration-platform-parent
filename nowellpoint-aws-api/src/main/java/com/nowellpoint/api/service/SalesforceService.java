package com.nowellpoint.api.service;

import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;

public interface SalesforceService {
	
	LoginResult login(String authEndpoint, String username, String password, String securityToken);
	
	DescribeGlobalSobjectsResult describe(String id);
	
	OauthAuthenticationResponse authenticate(String code);
	
	User getUser(String accessToken, String userId, String sobjectUrl);
	
	Organization getOrganization(String accessToken, String organizationId, String sobjectUrl);
	
	OauthAuthenticationResponse refreshToken(String refreshToken);
}
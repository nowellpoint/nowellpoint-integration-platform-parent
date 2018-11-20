package com.nowellpoint.console.service;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.QueryResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalResult;

public interface SalesforceService {
	public Token getToken(String authorizationCode);
	public Token refreshToken(String refreshToken);
	public Identity getIdentity(Token token);
	public Organization getOrganization(Token token);
	public DescribeGlobalResult describeGlobal(Token token);
	public QueryResult<UserLicense> getUserLicenses(Token token);
}
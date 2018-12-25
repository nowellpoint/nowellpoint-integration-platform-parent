package com.nowellpoint.client.sforce;

import java.util.Date;

import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;

public interface Salesforce {
	public Identity getIdentity(Token token);
	public DescribeGlobalResult describeGlobal(Token token);
	public User getUser(Token token);
	public Organization getOrganization(Token token);
	public DescribeResult describeSObject(Token token, String sobject, Date modifiedSince);
	public Theme getTheme(Token token);
}
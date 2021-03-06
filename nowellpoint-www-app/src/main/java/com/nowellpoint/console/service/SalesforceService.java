package com.nowellpoint.console.service;

import java.util.Set;

import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexTrigger;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Limits;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Profile;
import com.nowellpoint.client.sforce.model.RecordType;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.Resources;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.UserRole;
import com.nowellpoint.client.sforce.model.sobject.SObject;

public interface SalesforceService {
	public Token getToken(String authorizationCode);
	public Token refreshToken(String refreshToken);
	public Identity getIdentity(Token token);
	public Organization getOrganization(Token token);
	public DescribeGlobalResult describeGlobal(Token token);
	public UserLicense[] getUserLicenses(Token token);
	public Set<SObject> getCustomObjects(Token token);
	public Set<ApexClass> getApexClasses(Token token);
	public Set<ApexTrigger> getApexTriggers(Token token);
	public Set<RecordType> getRecordTypes(Token token);
	public Set<UserRole> getUserRoles(Token token);
	public Set<Profile> getProfiles(Token token);
	public Resources getResources(Token token);
	public Limits getLimits(Token token);
}
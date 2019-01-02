package com.nowellpoint.client.sforce;

import java.util.Date;
import java.util.Set;

import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexTrigger;
import com.nowellpoint.client.sforce.model.CreateResult;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Limits;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Profile;
import com.nowellpoint.client.sforce.model.PushTopic;
import com.nowellpoint.client.sforce.model.RecordType;
import com.nowellpoint.client.sforce.model.Resources;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.UserRole;

public interface Salesforce {
	public Identity getIdentity(Token token);
	public DescribeGlobalResult describeGlobal(Token token);
	public User getUser(Token token);
	public Organization getOrganization(Token token);
	public DescribeResult describeSObject(Token token, String sobject);
	public DescribeResult describeSObject(Token token, String sobject, Date modifiedSince);
	public Theme getTheme(Token token);
	public Long count(Token token, String query);
	public CreateResult createPushTopic(Token token, PushTopicRequest request);
	public PushTopic getPushTopic(Token token, String pushTopicId);
	public void updatePushTopic(Token token, String topicId, PushTopicRequest request);
	public void deletePushTopic(Token token, String topicId);
	public Set<UserLicense> getUserLicenses(Token token);
	public Set<ApexClass> getApexClasses(Token token);
	public Set<ApexTrigger> getApexTriggers(Token token);
	public Set<RecordType> getRecordTypes(Token token);
	public Set<UserRole> getUserRoles(Token token);
	public Set<Profile> getProfiles(Token token);
	public Resources getResources(Token token);
	public Limits getLimits(Token token);
}
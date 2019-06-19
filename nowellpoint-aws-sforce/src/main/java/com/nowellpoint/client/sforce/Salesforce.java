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
import com.nowellpoint.client.sforce.model.PushTopicRequest;
import com.nowellpoint.client.sforce.model.RecordType;
import com.nowellpoint.client.sforce.model.Resources;
import com.nowellpoint.client.sforce.model.SObject;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.User;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.UserRole;

public interface Salesforce {
	public static final String API_VERSION = "45.0";
	public static final String AUTHORIZE_URI = "https://login.salesforce.com/services/oauth2/authorize";
	public static final String TOKEN_URI = "https://login.salesforce.com/services/oauth2/token";
	public static final String REFRESH_URI = "https://login.salesforce.com/services/oauth2/refresh";
	public static final String REVOKE_URI = "https://login.salesforce.com/services/oauth2/revoke";
	
	public Identity getIdentity();
	public DescribeGlobalResult describeGlobal();
	public User getUser();
	public Organization getOrganization();
	public DescribeResult describeSObject(String sobject);
	public DescribeResult describeSObject(String sobject, Date modifiedSince);
	public Theme getTheme();
	public Long count(String query);
	public CreateResult createPushTopic(PushTopicRequest request);
	public PushTopic getPushTopic(String pushTopicId);
	public void updatePushTopic(String topicId, PushTopicRequest request);
	public void deletePushTopic(String topicId);
	public Set<com.nowellpoint.client.sforce.model.sobject.SObject> getCustomObjects();
	public UserLicense[] getUserLicenses();
	public Set<ApexClass> getApexClasses();
	public Set<ApexTrigger> getApexTriggers();
	public Set<RecordType> getRecordTypes();
	public Set<UserRole> getUserRoles();
	public Set<Profile> getProfiles();
	public Resources getResources();
	public Limits getLimits();
	public <T extends SObject> T findById(Class<T> type, String id);
	public <T> Set<T> query(Class<T> type, String query);
}
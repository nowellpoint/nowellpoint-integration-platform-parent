package com.nowellpoint.client.sforce;

import java.util.Date;
import java.util.Set;

import org.immutables.value.Value;

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

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractSalesforceClientBuilder {
	
	public Salesforce getClient() {
		return new SalesforceClient();
	}
	
	class SalesforceClient extends AbstractSalesforceClient implements Salesforce {

		@Override
		public Identity getIdentity(Token token) {
			return super.getIdentity(token);
		}
		
		@Override
		public DescribeGlobalResult describeGlobal(Token token) {
			return super.describeGlobal(token);
		}
		
		@Override
		public User getUser(Token token) {
			return super.getUser(token);
		}
		
		@Override
		public Organization getOrganization(Token token) {
			return super.getOrganization(token);
		}
		
		@Override
		public DescribeResult describeSObject(Token token, String sobject) {
			return super.describeSObject(token, sobject);
		}
		
		@Override
		public DescribeResult describeSObject(Token token, String sobject, Date modifiedSince) {
			return super.describeSObject(token, sobject, modifiedSince);
		}
		
		@Override
		public Theme getTheme(Token token) {
			return super.getTheme(token);
		}
		
		@Override
		public Long count(Token token, String query) {
			return super.count(token, query);
		}
		
		@Override
		public CreateResult createPushTopic(Token token, PushTopicRequest request) {
			return super.createPushTopic(token, request);
		}
		
		@Override
		public PushTopic getPushTopic(Token token, String pushTopicId) {
			return super.getPushTopic(token, pushTopicId);
		}
		
		@Override
		public void updatePushTopic(Token token, String topicId, PushTopicRequest request) {
			super.updatePushTopic(token, topicId, request);
		}
		
		@Override
		public void deletePushTopic(Token token, String topicId) {
			super.deletePushTopic(token, topicId);
		}
		
		@Override
		public Set<UserLicense> getUserLicenses(Token token) {
			return super.getUserLicenses(token);
		}
		
		@Override
		public Set<ApexClass> getApexClasses(Token token) {
			return super.getApexClasses(token);
		}
		
		@Override
		public Set<ApexTrigger> getApexTriggers(Token token) {
			return super.getApexTriggers(token);
		}
		
		@Override
		public Set<RecordType> getRecordTypes(Token token) {
			return super.getRecordTypes(token);
		}
		
		@Override
		public Set<UserRole> getUserRoles(Token token) {
			return super.getUserRoles(token);
		}
		
		@Override
		public Set<Profile> getProfiles(Token token) {
			return super.getProfiles(token);
		}
		
		@Override
		public Resources getResources(Token token) {
			return super.getResources(token);
		}
		
		@Override
		public Limits getLimits(Token token) {
			return super.getLimits(token);
		}
	}
}
package com.nowellpoint.client.sforce;

import java.util.Date;

import org.immutables.value.Value;

import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.User;

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
		public void deletePushTopic(Token token, String topicId) {
			super.deletePushTopic(token, topicId);
		}
	}
}
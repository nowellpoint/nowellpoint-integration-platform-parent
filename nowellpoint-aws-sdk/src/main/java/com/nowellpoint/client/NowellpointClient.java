package com.nowellpoint.client;

import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.resource.AccountProfileResource;
import com.nowellpoint.client.resource.IdentityResource;
import com.nowellpoint.client.resource.JobResource;
import com.nowellpoint.client.resource.JobTypeResource;
import com.nowellpoint.client.resource.PlanResource;
import com.nowellpoint.client.resource.SalesforceConnectorResource;
import com.nowellpoint.client.resource.SalesforceResource;

public interface NowellpointClient {
	
	public Token getToken();
	
	public void logout();
	
	public IdentityResource identity();
	
	public PlanResource plan();
	
	public JobResource job();
	
	public SalesforceConnectorResource salesforceConnector();
	
	public JobTypeResource scheduledJobType();
	
	public AccountProfileResource accountProfile();
	
	public SalesforceResource salesforce();

}
package com.nowellpoint.client;

import org.immutables.value.Value;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.resource.AccountProfileResource;
import com.nowellpoint.client.resource.IdentityResource;
import com.nowellpoint.client.resource.JobResource;
import com.nowellpoint.client.resource.JobTypeResource;
import com.nowellpoint.client.resource.PlanResource;
import com.nowellpoint.client.resource.SalesforceConnectorResource;
import com.nowellpoint.client.resource.SalesforceResource;
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public abstract class NowellpointSDK  {
	abstract Token token();
//	abstract IdentityResource identity();
//	abstract PlanResource plan();
//	abstract JobResource job();
//	abstract SalesforceConnectorResource salesforceConnector();
//	abstract JobTypeResource scheduledJobType();
//	abstract AccountProfileResource accountProfile();
//	abstract SalesforceResource salesforce();
	
	public static Builder builder() {
		return ImmutableNowellpointSDK.builder();
	}
	
	public interface Builder {
		Builder token(Token token);
		NowellpointSDK build();
	}
	
	public IdentityResource identity() {
		return new IdentityResource(token());
	}
	
	public PlanResource plan() {
		return new PlanResource(token());
	}
}
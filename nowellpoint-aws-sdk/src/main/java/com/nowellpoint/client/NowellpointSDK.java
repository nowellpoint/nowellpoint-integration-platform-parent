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

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
abstract class NowellpointSDK {
	abstract Token getToken();
	abstract IdentityResource identity();
	abstract PlanResource plan();
	abstract JobResource job();
	abstract SalesforceConnectorResource salesforceConnector();
	abstract JobTypeResource scheduledJobType();
	abstract AccountProfileResource accountProfile();
	abstract SalesforceResource salesforce();
	
	public static class Builder extends ImmutableNowellpointSDK.Builder {}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(getToken())
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);	
	}
}
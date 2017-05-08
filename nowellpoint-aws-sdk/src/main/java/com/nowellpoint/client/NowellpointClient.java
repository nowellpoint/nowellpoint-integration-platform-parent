package com.nowellpoint.client;

import org.immutables.value.Value;

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
public abstract class NowellpointClient  {
	abstract Token token();
	
	public static Builder builder() {
		return ImmutableNowellpointClient.builder();
	}
	
	public static NowellpointClient defaultClient(Token token) {
		return ImmutableNowellpointClient.builder()
				.token(token)
				.build();
	}
	
	public interface Builder {
		Builder token(Token token);
		NowellpointClient build();
	}
	
	public IdentityResource identity() {
		return new IdentityResource(token());
	}
	
	public PlanResource plan() {
		return new PlanResource(token());
	}
	
	public JobResource job() {
		return new JobResource(token());
	}
	
	public SalesforceConnectorResource salesforceConnector() {
		return new SalesforceConnectorResource(token());
	}
	
	public JobTypeResource scheduledJobType() {
		return new JobTypeResource(token());
	}
	
	public AccountProfileResource accountProfile() {
		return new AccountProfileResource(token());
	}
	
	public SalesforceResource salesforce() {
		return new SalesforceResource(token());
	}
}
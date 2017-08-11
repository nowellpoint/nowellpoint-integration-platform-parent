package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractOrganizationSetupRequest {
	public abstract String getOrganizationId();
	public abstract String getPlanId();
	//public abstract CreditCard
}
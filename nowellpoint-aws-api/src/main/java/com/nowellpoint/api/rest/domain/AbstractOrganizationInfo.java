package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractOrganizationInfo {
	public abstract String getId();
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();	
	public abstract String getNumber();
	public abstract String getDomain();
	public abstract Contact getBillingContact();
	public abstract Address getBillingAddress();
	public abstract Subscription getSubscription();
}
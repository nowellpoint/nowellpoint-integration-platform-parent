package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = SalesforceConnector.class)
@JsonDeserialize(as = SalesforceConnector.class)
public abstract class AbstractSalesforceConnector {
	public abstract Identity getIdentity();
	public abstract Organization getOrganization();
	public abstract String getServiceEndpoint();
}
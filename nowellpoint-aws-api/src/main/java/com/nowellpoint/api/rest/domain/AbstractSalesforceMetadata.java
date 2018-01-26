package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = SalesforceMetadata.class)
@JsonDeserialize(as = SalesforceMetadata.class)
public abstract class AbstractSalesforceMetadata {
	public abstract Identity getIdentity();
	public abstract Organization getOrganization();
	public abstract String getServiceEndpoint();
}
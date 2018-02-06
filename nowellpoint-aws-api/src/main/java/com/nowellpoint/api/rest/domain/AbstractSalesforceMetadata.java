package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = SalesforceMetadata.class)
@JsonDeserialize(as = SalesforceMetadata.class)
public abstract class AbstractSalesforceMetadata {
	public abstract String getServiceEndpoint();
	public abstract String getOrganizationId();
	public abstract String getInstanceName();
	public abstract String getOrganizationName();
}
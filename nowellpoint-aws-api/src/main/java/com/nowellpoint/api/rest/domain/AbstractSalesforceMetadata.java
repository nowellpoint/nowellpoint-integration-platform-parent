package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.util.Assert;

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
	
	public static SalesforceMetadata of(com.nowellpoint.api.model.document.SalesforceMetadata source) {
		if (Assert.isNull(source)) {
			return null;
		}
		
		SalesforceMetadata salesforceMetadata = SalesforceMetadata.builder()
				.instanceName(source.getInstanceName())
				.organizationId(source.getOrganizationId())
				.organizationName(source.getOrganizationName())
				.serviceEndpoint(source.getServiceEndpoint())
				.build();
		
		return salesforceMetadata;
	}
}
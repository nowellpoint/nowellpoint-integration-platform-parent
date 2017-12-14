package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.SalesforceConnectorResource;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new", depluralize = true, depluralizeDictionary = {"service:services"})
@JsonSerialize(as = SalesforceConnector.class)
@JsonDeserialize(as = SalesforceConnector.class)
public abstract class AbstractSalesforceConnector extends AbstractImmutableResource {
	public abstract String getName();
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract UserInfo getOwner();
	public abstract Identity getIdentity();
	public abstract Organization getOrganization();
	public abstract String getConnectionString();
	public abstract Date getLastTestedOn();
	public abstract Boolean getIsValid();
	public abstract String getServiceEndpoint();
	public abstract @Nullable String getStatus();
	public abstract @Nullable Theme getTheme();
	public abstract @Nullable Set<Service> getServices();
	
	@Override
	public Meta getMeta() {
		return resourceToMeta(SalesforceConnectorResource.class);
	}
	
	@Override
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(SalesforceConnector.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.SalesforceConnector.class);
	}
}
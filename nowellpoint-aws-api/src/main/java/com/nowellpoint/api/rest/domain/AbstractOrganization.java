package com.nowellpoint.api.rest.domain;

import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"referenceLink:referenceLinks"})
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization extends AbstractImmutableResource {
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract @Nullable String getNumber();
	public abstract String getDomain();
	public abstract @Nullable String getName();
	public abstract @Nullable AbstractSubscription getSubscription();
	public abstract @Nullable AbstractCreditCard getCreditCard();
	public abstract @Nullable AbstractAddress getBillingAddress();
	public abstract @Nullable AbstractContact getBillingContact();
	public abstract @Nullable Set<Transaction> getTransactions();
	public abstract @JsonIgnore Set<ReferenceLink> getReferenceLinks();
	
	@Override
	public Meta getMeta() {
		return getMetaAs(OrganizationResource.class);
	}
	
	public static Organization of(com.nowellpoint.api.model.document.Organization source) {
		ModifiableOrganization organization = ModifiableOrganization.create();
		modelMapper.map(source, organization);
		return organization.toImmutable();
	}
	
	@Override
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(AbstractOrganization.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Organization.class);
	}
}
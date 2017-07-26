package com.nowellpoint.api.rest.domain;

import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.Contact;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization extends AbstractImmutableResource {
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract @Nullable String getNumber();
	public abstract String getDomain();
	public abstract @Nullable String getName();
	public abstract @Nullable Contact getBillingContact();
	public abstract @Nullable Address getBillingAddress();
	public abstract @Nullable Subscription getSubscription();
	public abstract @Nullable Set<CreditCard> getCreditCards();
	public abstract @Nullable Set<Transaction> getTransactions();
	
	public static Organization of(MongoDocument document) {
		return modelMapper.map(document, Organization.class);
	}
	
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Organization.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Organization.class);
	}
}
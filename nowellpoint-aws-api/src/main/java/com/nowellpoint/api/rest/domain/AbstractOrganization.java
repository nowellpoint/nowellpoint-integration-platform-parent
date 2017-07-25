package com.nowellpoint.api.rest.domain;

import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.Contact;

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
}
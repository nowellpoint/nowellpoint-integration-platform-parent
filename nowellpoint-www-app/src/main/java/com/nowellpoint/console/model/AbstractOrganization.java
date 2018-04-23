package com.nowellpoint.console.model;

import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.OrganizationResource;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new", depluralize = true, depluralizeDictionary = {"transaction:transactions, user:users"})
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization {
	public abstract @Nullable String getId();
	public abstract String getNumber();
	public abstract String getDomain();
	public abstract @Nullable String getName();
	public abstract @Nullable Subscription getSubscription();
	public abstract @Nullable Set<Transaction> getTransactions();
	public abstract @Nullable Set<UserInfo> getUsers();
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(OrganizationResource.class)
				.build();
	}
}
package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.OrganizationResource;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new", depluralize = true, depluralizeDictionary = {"transaction:transactions"})
@JsonSerialize(as = OrganizationInfo.class)
@JsonDeserialize(as = OrganizationInfo.class)
public abstract class AbstractOrganizationInfo {
	public abstract String getId();
	public abstract String getNumber();
	public abstract String getDomain();
	public abstract String getPlan();
	public abstract String getName();
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(OrganizationResource.class)
				.build();
	}
	
	public static OrganizationInfo of(com.nowellpoint.console.entity.Organization source) {
		return source == null ? null : OrganizationInfo.builder()
				.domain(source.getDomain())
				.id(source.getId().toString())
				.name(source.getName())
				.number(source.getNumber())
				.plan(source.getSubscription().getPlanName())
				.build();
	}
}
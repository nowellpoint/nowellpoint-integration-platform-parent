package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Identity.class)
@JsonDeserialize(as = Identity.class)
public abstract class AbstractIdentity {
	public abstract String getId();
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getName();
	public abstract AbstractOrganization getOrganization();
	public abstract String getTimeZone();
	public abstract String getLocale();
	public abstract Address getAddress();
	public abstract Resources getResources();
	public abstract Meta getMeta();
}
package com.nowellpoint.api.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = RegistrationRequest.class)
@JsonDeserialize(as = RegistrationRequest.class)
public abstract class AbstractRegistrationRequest {
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract String getCountryCode();
	public abstract @Nullable String getDomain();
	public abstract String getPlanId();
}
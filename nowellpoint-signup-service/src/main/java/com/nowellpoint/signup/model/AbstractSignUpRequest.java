package com.nowellpoint.signup.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = SignUpRequest.class)
@JsonDeserialize(as = SignUpRequest.class)
public abstract class AbstractSignUpRequest {
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract String getCountryCode();
	public abstract @Nullable String getDomain();
	public abstract String getPlanId();
}
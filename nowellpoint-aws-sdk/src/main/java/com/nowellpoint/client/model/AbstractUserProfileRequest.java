package com.nowellpoint.client.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = UserProfileRequest.class)
public abstract class AbstractUserProfileRequest extends AbstractAuthenticatedRequest {
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract @Nullable String getTitle();
	public abstract String getLocale();
	public abstract String getTimeZone();
}
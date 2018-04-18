package com.nowellpoint.oauth.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = UserInfoResponse.class)
@JsonDeserialize(as = UserInfoResponse.class)
public abstract class AbstractUserInfoResponse {
	@JsonProperty(value="sub") public abstract String getSubject();
	@JsonProperty(value="name") public abstract String getName();
	@JsonProperty(value="nickname") public abstract String getNickname();
	@JsonProperty(value="given_name") public abstract String getGivenName();
	@JsonProperty(value="middle_name") public abstract String getMiddleName();
	@JsonProperty(value="family_name") public abstract String getFamilyName();
	@JsonProperty(value="profile") public abstract String getProfile();
	@JsonProperty(value="zoneinfo") public abstract String getTimeZone();
	@JsonProperty(value="locale") public abstract String getLocale();
	@JsonProperty(value="email") public abstract String getEmail();
	@JsonProperty(value="email_verified") public abstract String getEmailVerified();
	@JsonProperty(value="phone_number") public abstract String getPhoneNumber();
	@JsonProperty(value="address") public abstract Address getAddress();
}
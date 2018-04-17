package com.nowellpoint.console.service;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = TokenResponse.class)
@JsonDeserialize(as = TokenResponse.class)
public abstract class AbstractTokenResponse {
	@JsonProperty(value="access_token") public abstract String getAccessToken();
	@JsonProperty(value="token_type") public abstract String getTokenType();
	@JsonProperty(value="expires_in") public abstract Long getExpiresIn();
	@JsonProperty(value="scope") public abstract String getScope();
	@JsonProperty(value="refresh_token") @Nullable public abstract String getRefreshToken();
}
package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = Token.class)
@JsonDeserialize(as = Token.class)
public abstract class AbstractToken {
	@JsonProperty(value="id") public abstract String getId();
	@JsonProperty(value="environment_url") public abstract String getEnvironmentUrl();
	@JsonProperty(value="access_token") public abstract String getAccessToken();
	@JsonProperty(value="refresh_token") @Nullable public abstract String getRefreshToken();
	@JsonProperty(value="token_type") public abstract String getTokenType();
	@JsonProperty(value="expires_in") public abstract Long getExpiresIn();
}
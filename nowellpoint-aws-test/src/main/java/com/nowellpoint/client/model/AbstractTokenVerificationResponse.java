package com.nowellpoint.client.model;

import java.util.Collections;
import java.util.List;

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
@JsonSerialize(as = TokenVerificationResponse.class)
@JsonDeserialize(as = TokenVerificationResponse.class)
public abstract class AbstractTokenVerificationResponse {
	@JsonProperty(value="active") public abstract Boolean getActive();
	@JsonProperty(value="scope") @Nullable public abstract String getScope();
	@JsonProperty(value="username") @Nullable public abstract String getUsername();
	@JsonProperty(value="exp") @Nullable public abstract Long getExpirationTime();
	@JsonProperty(value="iat") @Nullable public abstract Long getIssuedAt();
	@JsonProperty(value="sub") @Nullable public abstract String getSubject();
	@JsonProperty(value="aud") @Nullable public abstract String getAudience();
	@JsonProperty(value="iss") @Nullable public abstract String getIssuer();
	@JsonProperty(value="jti") @Nullable public abstract String getId();
	@JsonProperty(value="token_type") @Nullable public abstract String getTokenType();
	@JsonProperty(value="client_id") @Nullable public abstract String getClientId();
	@JsonProperty(value="uid") @Nullable public abstract String getUserId();
	@JsonProperty(value="groups") @Value.Default public List<String> getGroups() { return Collections.emptyList(); };
}
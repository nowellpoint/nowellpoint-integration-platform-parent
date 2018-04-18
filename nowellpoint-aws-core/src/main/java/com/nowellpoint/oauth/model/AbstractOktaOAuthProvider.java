package com.nowellpoint.oauth.model;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractOktaOAuthProvider implements OAuthProviderType {

	@Override
	@Value.Default
	public String getAuthorizationServer() {
		return Optional.ofNullable(System.getenv("OKTA_AUTHORIZATION_SERVER")).isPresent() ? 
				System.getenv("OKTA_AUTHORIZATION_SERVER") : 
					Optional.ofNullable(System.getProperty("okta.authorization.server")).orElseThrow(() -> new IllegalArgumentException("Unable to find a value for authorization server"));
	}

	@Override
	@Value.Default
	public String getClientId() {
		return Optional.ofNullable(System.getenv("OKTA_CLIENT_ID")).isPresent() ?
				System.getenv("OKTA_CLIENT_ID") :
					Optional.ofNullable(System.getProperty("okta.client.id")).orElseThrow(() -> new IllegalArgumentException("Unable to find a value for client id"));
	}

	@Override
	@Value.Default
	public String getClientSecret() {
		return Optional.ofNullable(System.getenv("OKTA_CLIENT_SECRET")).isPresent() ? 
				System.getenv("OKTA_CLIENT_SECRET") : 
					Optional.ofNullable(System.getProperty("okta.authorization.server")).orElseThrow(() -> new IllegalArgumentException("Unable to find a value for client secret"));
	}
}
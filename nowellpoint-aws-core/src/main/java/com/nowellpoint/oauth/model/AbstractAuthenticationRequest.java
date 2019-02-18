package com.nowellpoint.oauth.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractAuthenticationRequest {
	public abstract String getUsername();
	public abstract String getPassword();
}
package com.nowellpoint.client.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractUpdateRegistrationRequest {
	public abstract @Nullable String getDomain();
	public abstract @Nullable String getPlanId();
}
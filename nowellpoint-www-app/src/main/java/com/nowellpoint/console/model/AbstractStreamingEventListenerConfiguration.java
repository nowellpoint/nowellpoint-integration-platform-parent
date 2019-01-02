package com.nowellpoint.console.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, get = {"is*", "get*"})
public abstract class AbstractStreamingEventListenerConfiguration {
	public abstract Boolean isActive();
	public abstract String getOrganizationId();
	public abstract String getRefreshToken();
	public abstract String getTopicId();
	public abstract String getApiVersion();
	public abstract String getChannel();
	public abstract String getSource();
}
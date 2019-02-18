package com.nowellpoint.console.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, get = {"is*", "get*"})
public abstract class AbstractStreamingEventListenerRequest {
	public abstract String getSource();
	public abstract Boolean getNotifyForOperationUpdate();
	public abstract Boolean getNotifyForOperationCreate();
	public abstract Boolean getNotifyForOperationDelete();
	public abstract Boolean getNotifyForOperationUndelete();
	public abstract Boolean isActive();
}
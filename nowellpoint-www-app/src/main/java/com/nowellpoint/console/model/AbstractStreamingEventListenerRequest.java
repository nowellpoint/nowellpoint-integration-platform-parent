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
	
	@Value.Default
	public Boolean getOnCreate() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getOnUpdate() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getOnDelete() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getOnUndelete() {
		return Boolean.FALSE;
	}
}
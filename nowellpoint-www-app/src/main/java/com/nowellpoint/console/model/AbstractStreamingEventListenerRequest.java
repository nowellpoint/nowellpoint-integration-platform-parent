package com.nowellpoint.console.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, get = {"is*", "get*"})
public abstract class AbstractStreamingEventListenerRequest {
	public abstract String getSource();
	
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
	
	@Value.Default
	public Boolean isActive() {
		return Boolean.FALSE;
	}
}
package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractStreamingEventListenerRequest {
	public abstract @Nullable String getId();
	public abstract String getObject();
	
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
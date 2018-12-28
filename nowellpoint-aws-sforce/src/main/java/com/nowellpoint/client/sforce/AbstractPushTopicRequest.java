package com.nowellpoint.client.sforce;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true, get = {"is*", "get*"})
@JsonSerialize(as = PushTopicRequest.class)
@JsonDeserialize(as = PushTopicRequest.class)
public abstract class AbstractPushTopicRequest {
	public abstract String getName();
	public abstract String getQuery();
	public abstract String getDescription();
	
	@Value.Default
	public Boolean getNotifyForOperationCreate() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getNotifyForOperationUpdate() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getNotifyForOperationUndelete() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getNotifyForOperationDelete() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean isActive() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public String getApiVersion() {
		return "44.0";
	}
	
	@Value.Default
	public String getNotifyForFields() {
		return "All";
	}
}
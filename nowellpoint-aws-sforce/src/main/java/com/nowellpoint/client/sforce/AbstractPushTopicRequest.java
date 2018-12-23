package com.nowellpoint.client.sforce;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = PushTopicRequest.class)
@JsonDeserialize(as = PushTopicRequest.class)
public abstract class AbstractPushTopicRequest {
	public abstract String getName();
	public abstract String getQuery();
	public abstract String getApiVersion();
	public abstract Boolean getNotifyForOperationCreate();
	public abstract Boolean getNotifyForOperationUpdate();
	public abstract Boolean getNotifyForOperationUndelete();
	public abstract Boolean getNotifyForOperationDelete();
	public abstract String getNotifyForFields();
}
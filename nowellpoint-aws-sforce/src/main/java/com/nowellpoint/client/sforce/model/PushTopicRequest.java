package com.nowellpoint.client.sforce.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PushTopicRequest {
	private String name;
	private String query;
	private String description;
	private @Builder.Default Boolean notifyForOperationCreate = Boolean.FALSE;
	private @Builder.Default Boolean notifyForOperationUpdate = Boolean.FALSE;
	private @Builder.Default Boolean notifyForOperationUndelete = Boolean.FALSE;
	private @Builder.Default Boolean notifyForOperationDelete = Boolean.FALSE;
	private @Builder.Default Boolean isActive = Boolean.FALSE;
	private @Builder.Default String apiVersion = "44.0";
	private @Builder.Default String notifyForFields = "All";
}
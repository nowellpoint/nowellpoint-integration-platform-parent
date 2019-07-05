package com.nowellpoint.listener.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QueueConfiguration {
	private String queueName;
	private String queueUrl;
	private Boolean active;
	private String listenerClass;
	private String queueType;
	private String eventType;
}
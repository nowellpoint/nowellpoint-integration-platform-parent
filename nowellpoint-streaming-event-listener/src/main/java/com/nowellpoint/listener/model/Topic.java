package com.nowellpoint.listener.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Topic {
	private String channel;
	private String source;
	private String topicId;
	private Boolean active;
}
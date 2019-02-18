package com.nowellpoint.listener.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopicConfiguration {
	private String organizationId;
	private String apiVersion;
	private String refreshToken;
	private List<Topic> topics;
}
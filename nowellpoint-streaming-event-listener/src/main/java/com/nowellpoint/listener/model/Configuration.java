package com.nowellpoint.listener.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Configuration {
	private String organizationId;
	private String refreshToken;
	private List<Topic> topics;
}
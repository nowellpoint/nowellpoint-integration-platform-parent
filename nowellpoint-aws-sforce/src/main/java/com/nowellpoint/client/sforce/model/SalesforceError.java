package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SalesforceError {
	private @JsonProperty("error") String error;
	private @JsonProperty("errorDescription") String errorDescription;
	private @JsonProperty("message") String messag;
	private @JsonProperty("errorCode") String errorCode;
}
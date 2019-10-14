package com.nowellpoint.client.sforce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError {
	@Getter @JsonProperty("message") private String message;
	@Getter @JsonProperty("errorCode") private String errorCode;
	@Getter @JsonProperty("fields") private String[] fields;
	@Getter @JsonProperty("error") private String error;
	@Getter @JsonProperty("error_description") private String errorDescription;
	
	public ApiError() { }
}
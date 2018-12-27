package com.nowellpoint.client.sforce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError {
	public @JsonProperty("message") String message;
	public @JsonProperty("errorCode") String errorCode;
	public @JsonProperty("fields") String[] fields;
	
	public ApiError() {
		
	}

	public String getMessage() {
		return message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String[] getFields() {
		return fields;
	}
}
package com.nowellpoint.client.sforce.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Error implements Serializable {

	private static final long serialVersionUID = -4556365201148471897L;
	
	@Getter @JsonProperty("ids") private List<String> fields;
	@Getter @JsonProperty("message") private String message;
	@Getter @JsonProperty("errorCode") private String errorCode;
	@Getter @JsonProperty(value="error_description") private String errorDescription;
	
	public Error() { }
}
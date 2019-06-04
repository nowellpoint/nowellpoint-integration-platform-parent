package com.nowellpoint.client.sforce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class CreateResult {
	@Getter @JsonProperty(value="id") private String id;
	@Getter @JsonProperty(value="success") private Boolean success;
	@Getter @JsonProperty(value="errors") private List<Error> errors;
}
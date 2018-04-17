package com.nowellpoint.console.service;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = Error.class)
@JsonDeserialize(as = Error.class)
public abstract class AbstractError {
	@JsonProperty(value="error") public abstract String getError();
	@JsonProperty(value="error_description") public abstract String getErrorDescription();
}
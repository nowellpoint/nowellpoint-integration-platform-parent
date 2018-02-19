package com.nowellpoint.api.rest.domain;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"message:messages"})
@JsonSerialize(as = Error.class)
@JsonDeserialize(as = Error.class)
public abstract class AbstractError {
	public abstract String getCode();
	public abstract Set<String> getMessages();
}
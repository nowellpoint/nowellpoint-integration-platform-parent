package com.nowellpoint.oauth.model;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = Keys.class)
@JsonDeserialize(as = Keys.class)
public abstract class AbstractKeys {
	@JsonProperty(value="keys") public abstract Set<Key> getKeys();
}
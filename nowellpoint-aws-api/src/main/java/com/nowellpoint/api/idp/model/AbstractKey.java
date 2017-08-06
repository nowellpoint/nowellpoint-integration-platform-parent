package com.nowellpoint.api.idp.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = Key.class)
@JsonDeserialize(as = Key.class)
public abstract class AbstractKey {
	@JsonProperty(value="alg") public abstract String getAlgorithm();
	@JsonProperty(value="kid") public abstract String getKeyId();
	@JsonProperty(value="kty") public abstract String getKeyType();
	@JsonProperty(value="e") public abstract String getExponent();
	@JsonProperty(value="n") public abstract String getModulus();
	@JsonProperty(value="use") public abstract String getUseage();
}
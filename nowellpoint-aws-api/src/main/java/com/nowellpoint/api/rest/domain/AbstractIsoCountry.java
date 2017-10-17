package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new", depluralize = true)
@JsonSerialize(as = IsoCountry.class)
@JsonDeserialize(as = IsoCountry.class)
public abstract class AbstractIsoCountry implements Resource {
	public abstract String getIso2Code();
	public abstract String getName();

	@Override
	@JsonIgnore
	public Meta getMeta() {
		return null;
	}
}
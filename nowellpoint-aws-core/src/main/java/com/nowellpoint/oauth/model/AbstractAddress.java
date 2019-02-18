package com.nowellpoint.oauth.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = Address.class)
@JsonDeserialize(as = Address.class)
public abstract class AbstractAddress {
	@JsonProperty(value="street_address") public abstract String getStreetAddress();
	@JsonProperty(value="locality") public abstract String getLocality();
	@JsonProperty(value="region") public abstract String getRegion();
	@JsonProperty(value="postal_code") public abstract String getPostalCode();
	@JsonProperty(value="country") public abstract String getCountry();
}
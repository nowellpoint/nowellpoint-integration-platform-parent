package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Address.class)
@JsonDeserialize(as = Address.class)
public abstract class AbstractAddress {
	public abstract @Nullable String getStreet();
	public abstract @Nullable String getCity();
	public abstract @Nullable String getStateCode();
	public abstract @Nullable String getState();
	public abstract String getCountryCode();
	public abstract String getCountry();
	public abstract @Nullable String getLatitude();
	public abstract @Nullable String getLongitude();
}
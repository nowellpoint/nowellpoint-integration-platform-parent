package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = AddressRequest.class)
@JsonDeserialize(as = AddressRequest.class)
public abstract class AbstractAddressRequest {
	public abstract String getStreet();
	public abstract String getCity();
	public abstract @Nullable String getState();
	public abstract String getPostalCode();
	public abstract String getCountryCode();
}
package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Address.class)
@JsonDeserialize(as = Address.class)
public abstract class AbstractAddress {
	public abstract @Nullable String getId();
	public abstract @Nullable String getStreet();
	public abstract @Nullable String getCity();
	public abstract @Nullable String getStateCode();
	public abstract @Nullable String getPostalCode();
	public abstract String getCountryCode();
	public abstract @Nullable String getLatitude();
	public abstract @Nullable String getLongitude();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getAddedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	
	public String getCountry() {
		return ResourceBundle.getBundle("countries", Locale.getDefault()).getString(getCountryCode());
	}
	
	public String getState() {
		if (Assert.isNotNullOrEmpty(getStateCode())) {
			return ResourceBundle.getBundle("states", Locale.getDefault()).getString(getStateCode());
		} else {
			return null;
		}
	}
	
	public static Address of(com.nowellpoint.api.model.document.Address source) {
		Address instance = ModifiableAddress.create()
				.setAddedOn(source.getAddedOn())
				.setCity(source.getCity())
				.setCountryCode(source.getCountryCode())
				.setId(source.getId())
				.setLatitude(source.getLatitude())
				.setLongitude(source.getLongitude())
				.setPostalCode(source.getPostalCode())
				.setStateCode(source.getStateCode())
				.setStreet(source.getStreet())
				.setUpdatedOn(source.getUpdatedOn())
				.toImmutable();
		
		return instance;
	}
	
	public static Address of(com.braintreegateway.Address source) {
		Address instance = Address.builder()
				.street(source.getStreetAddress())
				.city(source.getLocality())
				.countryCode(source.getCountryCodeAlpha2())
				.id(source.getId())
				.postalCode(source.getPostalCode())
				.stateCode(source.getRegion())
				.addedOn(source.getCreatedAt().getTime())
				.updatedOn(source.getUpdatedAt().getTime())
				.build();
		
		return instance;
	}
}
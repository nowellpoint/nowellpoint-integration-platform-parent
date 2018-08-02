package com.nowellpoint.console.model;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Address.class)
@JsonDeserialize(as = Address.class)
public abstract class AbstractAddress {
	public abstract @Nullable String getId();
	public abstract @Nullable String getStreet();
	public abstract @Nullable String getCity();
	public abstract @Nullable String getState();
	//public abstract @Nullable String getStateCode();
	public abstract @Nullable String getPostalCode();
	public abstract @Nullable String getLatitude();
	public abstract @Nullable String getLongitude();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getAddedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	
	@Value.Default
	public String getCountryCode() {
		return Locale.getDefault().getCountry();
	}
	
	@Value.Derived
	public String getCountry() {
		return ResourceBundle.getBundle("countries", Locale.getDefault()).getString(getCountryCode());
	}
	
//	public String getState() {
//		if (Assert.isNotNullOrEmpty(getStateCode())) {
//			return ResourceBundle.getBundle("states", Locale.getDefault()).getString(getStateCode());
//		} else {
//			return null;
//		}
//	}
	
	public static Address of(com.nowellpoint.console.entity.Address source) {
		return source == null ? null : Address.builder()
				.addedOn(source.getAddedOn())
				.city(source.getCity())
				.countryCode(source.getCountryCode())
				.id(source.getId())
				.latitude(source.getLatitude())
				.longitude(source.getLongitude())
				.postalCode(source.getPostalCode())
				.state(source.getState())
				.street(source.getStreet())
				.updatedOn(source.getUpdatedOn())
				.build();
	}
	
	public static Address of(com.braintreegateway.Address source) {
		return source == null ? null : Address.builder()
				.addedOn(source.getCreatedAt().getTime())
				.city(source.getLocality())
				.countryCode(source.getCountryCodeAlpha2())
				.id(source.getId())
				.postalCode(source.getPostalCode())
				.state(source.getRegion())
				.street(source.getStreetAddress())
				.updatedOn(source.getUpdatedAt().getTime())
				.build();
	}
}
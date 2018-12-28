package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Serializable {
	
	private static final long serialVersionUID = 3317676775699599582L;
	private @JsonProperty(value="street") String street;
	private @JsonProperty(value="city") String city;
	private @JsonProperty(value="state") String state;
	private @JsonProperty(value="stateCode") String stateCode;
	private @JsonProperty(value="country") String country;
	private @JsonProperty(value="countryCode") String countryCode;
	private @JsonProperty(value="postalCode") String postalCode;
	private @JsonProperty(value="longitude")String longitude;
	private @JsonProperty(value="latitude") String latitude;
	
	public Address() {
		
	}
	
	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getStateCode() {
		return stateCode;
	}

	public String getCountry() {
		return country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}
}
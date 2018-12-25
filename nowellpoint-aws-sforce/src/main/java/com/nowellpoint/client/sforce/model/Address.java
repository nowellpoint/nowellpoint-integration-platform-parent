package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3317676775699599582L;

	@JsonProperty(value="street")
	private String street;
	
	@JsonProperty(value="city")
	private String city;
	
	@JsonProperty(value="state")
	private String state;
	
	@JsonProperty(value="stateCode")
	private String stateCode;
	
	@JsonProperty(value="country")
	private String country;
	
	@JsonProperty(value="countryCode")
	private String countryCode;
	
	@JsonProperty(value="postalCode")
	private String postalCode;
	
	@JsonProperty(value="longitude")
	private String longitude;
	
	@JsonProperty(value="latitude")
	private String latitude;
	
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
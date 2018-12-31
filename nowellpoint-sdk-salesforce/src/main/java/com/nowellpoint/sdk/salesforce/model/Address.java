package com.nowellpoint.sdk.salesforce.model;

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

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
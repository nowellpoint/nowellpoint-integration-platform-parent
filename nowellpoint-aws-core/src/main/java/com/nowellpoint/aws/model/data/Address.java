package com.nowellpoint.aws.model.data;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Address implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8625374697414134673L;
	
	/**
	 * 
	 */

	private String street;

	/**
	 * 
	 */
	
	private String city;

	/**
	 * 
	 */

	private String state;

	/**
	 * 
	 */

	private String postalCode;

	/**
	 * 
	 */

	private String countryCode;

	/**
	 * 
	 */

	private String latitude;

	/**
	 * 
	 */

	private String longitude;

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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
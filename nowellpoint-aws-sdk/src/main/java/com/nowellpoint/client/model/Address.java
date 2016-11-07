package com.nowellpoint.client.model;

public class Address {
	
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

	private String country;
	
	/**
	 * 
	 */

	private String latitude;

	/**
	 * 
	 */

	private String longitude;
	
	/**
	 * 
	 */

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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public Address withStreet(String street) {
		setStreet(street);
		return this;
	}
	
	public Address withCity(String city) {
		setCity(city);
		return this;
	}
	
	public Address withState(String state) {
		setState(state);
		return this;
	}

	public Address withPostalCode(String postalCode) {
		setPostalCode(postalCode);
		return this;
	}
	
	public Address withCountryCode(String countryCode) {
		setCountryCode(countryCode);
		return this;
	}

	public Address withCountry(String country) {
		setCountry(country);
		return this;
	}
	
	public Address withLatitude(String latitude) {
		setLatitude(latitude);
		return this;
	}
	
	public Address withLongitude(String longitude) {
		setLongitude(longitude);
		return this;
	}
}
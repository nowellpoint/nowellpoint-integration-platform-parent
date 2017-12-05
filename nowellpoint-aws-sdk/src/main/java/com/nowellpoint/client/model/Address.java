package com.nowellpoint.client.model;

import java.util.Date;

public class Address {
	
	private String id;
	
	private String street;

	private String city;
	
	private String stateCode;

	private String state;

	private String postalCode;

	private String countryCode;
	
	private String country;
	
	private String latitude;

	private String longitude;
	
	private Date addedOn;
	
	private Date updatedOn;
	
	public Address() {
		
	}
	
	public String getId() {
		return id;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getStateCode() {
		return stateCode;
	}

	public String getState() {
		return state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getCountry() {
		return country;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}
}
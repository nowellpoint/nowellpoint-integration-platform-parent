package com.nowellpoint.client.model;

import java.util.Locale;
import java.util.TimeZone;

public class Identity {

	private String id;
	
	private String firstName;
	
	private String lastName;
	
	private String name;
	
	private TimeZone timeZone;

	private Locale locale;
	
	private Organization organization;
	
	private Address address;
	
	private Resources resources;
	
	private Meta meta;
	
	public Identity() {
		
	}

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getName() {
		return name;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public Locale getLocale() {
		return locale;
	}

	public Organization getOrganization() {
		return organization;
	}

	public Address getAddress() {
		return address;
	}

	public Resources getResources() {
		return resources;
	}

	public Meta getMeta() {
		return meta;
	}
}
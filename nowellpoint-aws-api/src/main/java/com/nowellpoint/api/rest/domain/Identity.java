package com.nowellpoint.api.rest.domain;

import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.model.document.Address;

public class Identity {
	
	private String id;
	
	private String firstName;
	
	private String lastName;
	
	private String name;
	
	private Subscription subscription;
	
	private String timeZoneSidKey;
	
	private String languageSidKey;

	private String localeSidKey;
	
	private Address address;
	
	private Resources resources;
	
	private Meta meta;
	
	private Identity(AccountProfile accountProfile, UriInfo uriInfo) {
		this.id = accountProfile.getId();
		this.firstName = accountProfile.getFirstName();
		this.lastName = accountProfile.getLastName();
		this.name = accountProfile.getName();
		this.subscription = accountProfile.getSubscription();
		this.address = accountProfile.getAddress();
		this.languageSidKey = accountProfile.getLanguageSidKey();
		this.localeSidKey = accountProfile.getLocaleSidKey();
		this.timeZoneSidKey = accountProfile.getTimeZoneSidKey();
		this.meta = accountProfile.getMeta();
		this.resources = Resources.of(uriInfo);
	}
	
	public static Identity of(AccountProfile accountProfile, UriInfo uriInfo) {
		return new Identity(accountProfile, uriInfo);
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

	public Subscription getSubscription() {
		return subscription;
	}

	public String getTimeZoneSidKey() {
		return timeZoneSidKey;
	}

	public String getLanguageSidKey() {
		return languageSidKey;
	}

	public String getLocaleSidKey() {
		return localeSidKey;
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
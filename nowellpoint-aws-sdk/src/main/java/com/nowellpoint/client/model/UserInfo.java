package com.nowellpoint.client.model;

public class UserInfo {
	
	private String id; 
	
	private Meta meta;
	
	private String lastName;

	private String firstName;

	private String name;

	private String email;

	private String phone;
	
	private Photos photos;
	
	public UserInfo() {
		
	}
	
	public UserInfo(String id) {
		this.id = id;
	}

	public Meta getMeta() {
		return meta;
	}

	public String getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public Photos getPhotos() {
		return photos;
	}
}
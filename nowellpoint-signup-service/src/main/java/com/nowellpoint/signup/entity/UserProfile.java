package com.nowellpoint.signup.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "user.profiles")
public class UserProfile extends BaseEntity {
	
	private String username;

	private String lastName;

	private String firstName;

	private String name;

	private String title;

	private String email;

	private String phone;

	private Boolean isActive;
	
	public UserProfile() {
		
	}
	
	public UserProfile(String id) {
		setId(new ObjectId(id));
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}
package com.nowellpoint.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileRequest extends AbstractResource {
	
	/**
	 * 
	 */

	private String username;

	/**
	 * 
	 */

	private String lastName;

	/**
	 * 
	 */

	private String firstName;

	/**
	 * 
	 */

	private String name;

	/**
	 * 
	 */

	private String company;

	/**
	 * 
	 */

	private String division;

	/**
	 * 
	 */

	private String department;

	/**
	 * 
	 */

	private String title;

	/**
	 * 
	 */

	private String email;

	/**
	 * 
	 */

	private String phone;

	/**
	 * 
	 */

	private String extension;

	/**
	 * 
	 */

	private String mobilePhone;

	/**
	 * 
	 */

	private Boolean isActive;

	/**
	 * 
	 */

	private String timeZone;

	/**
	 * 
	 */

	private String locale;
	
	/**
	 * 
	 */
	
	public UserProfileRequest() {

	}
	
	public UserProfileRequest(String id) {
		this();
		this.setId(id);
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
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

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public UserProfileRequest withId(String id) {
		setId(id);
		return this;
	}
	
	public UserProfileRequest withLastName(String lastName) {
		setLastName(lastName);
		return this;
	}
	
	public UserProfileRequest withFirstName(String firstName) {
		setFirstName(firstName);
		return this;
	}
	
	public UserProfileRequest withCompany(String company) {
		setCompany(company);
		return this;
	}
	
	public UserProfileRequest withDivision(String division) {
		setDivision(division);
		return this;
	}
	
	public UserProfileRequest withDepartment(String department) {
		setDepartment(department);
		return this;
	}
	
	public UserProfileRequest withTitle(String title) {
		setTitle(title);
		return this;
	}
	
	public UserProfileRequest withEmail(String email) {
		setEmail(email);
		return this;
	}
	
	public UserProfileRequest withPhone(String phone) {
		setPhone(phone);
		return this;
	}
	
	public UserProfileRequest withExtension(String extension) {
		setExtension(extension);
		return this;
	}
	
	public UserProfileRequest withMobilePhone(String mobilePhone) {
		setMobilePhone(mobilePhone);
		return this;
	}
	
	public UserProfileRequest withLocale(String locale) {
		setLocale(locale);
		return this;
	}
	
	public UserProfileRequest withTimeZone(String timeZone) {
		setTimeZone(timeZone);
		return this;
	}
}
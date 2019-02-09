package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends SObject {

	private static final long serialVersionUID = 3163086585922281575L;
	
	@JsonProperty("AboutMe")
	private String aboutMe;

	@JsonProperty("Username")
	private String username;

	@JsonProperty("LastName")
	private String lastName;

	@JsonProperty("FirstName")
	private String firstName;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Company")
	private String company;

	@JsonProperty("Division")
	private String division;

	@JsonProperty("Department")
	private String department;

	@JsonProperty("Title")
	private String title;

	@JsonProperty("Street")
	private String street;

	@JsonProperty("City")
	private String city;

	@JsonProperty("State")
	private String state;

	@JsonProperty("PostalCode")
	private String postalCode;

	@JsonProperty("Country")
	private String country;

	@JsonProperty("Latitude")
	private String latitude;

	@JsonProperty("Longitude")
	private String longitude;

	@JsonProperty("Email")
	private String email;

	@JsonProperty("SenderEmail")
	private String senderEmail;

	@JsonProperty("SenderName")
	private String senderName;

	@JsonProperty("Signature")
	private String signature;

	@JsonProperty("Phone")
	private String phone;

	@JsonProperty("Extension")
	private String extension;

	@JsonProperty("Fax")
	private String fax;

	@JsonProperty("MobilePhone")
	private String mobilePhone;

	@JsonProperty("Alias")
	private String alias;

	@JsonProperty("CommunityNickname")
	private String communityNickname;

	@JsonProperty("IsActive")
	private Boolean isActive;

	@JsonProperty("TimeZoneSidKey")
	private String timeZoneSidKey;

	@JsonProperty("LocaleSidKey")
	private String localeSidKey;

	@JsonProperty("EmailEncodingKey")
	private String emailEncodingKey;

	@JsonProperty("PermissionsCustomizeApplication")
	private Boolean permissionsCustomizeApplication;

	@JsonProperty("UserType")
	private String userType;

	@JsonProperty("LanguageLocaleKey")
	private String languageLocaleKey;

	@JsonProperty("EmployeeNumber")
	private String employeeNumber;

	public User() {

	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public String getAboutMe() {
		return aboutMe;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
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

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getCommunityNickname() {
		return communityNickname;
	}

	public void setCommunityNickname(String communityNickname) {
		this.communityNickname = communityNickname;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getTimeZoneSidKey() {
		return timeZoneSidKey;
	}

	public void setTimeZoneSidKey(String timeZoneSidKey) {
		this.timeZoneSidKey = timeZoneSidKey;
	}

	public String getLocaleSidKey() {
		return localeSidKey;
	}

	public void setLocaleSidKey(String localeSidKey) {
		this.localeSidKey = localeSidKey;
	}

	public String getEmailEncodingKey() {
		return emailEncodingKey;
	}

	public void setEmailEncodingKey(String emailEncodingKey) {
		this.emailEncodingKey = emailEncodingKey;
	}

	public Boolean getPermissionsCustomizeApplication() {
		return permissionsCustomizeApplication;
	}

	public void setPermissionsCustomizeApplication(Boolean permissionsCustomizeApplication) {
		this.permissionsCustomizeApplication = permissionsCustomizeApplication;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getLanguageLocaleKey() {
		return languageLocaleKey;
	}

	public void setLanguageLocaleKey(String languageLocaleKey) {
		this.languageLocaleKey = languageLocaleKey;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
}
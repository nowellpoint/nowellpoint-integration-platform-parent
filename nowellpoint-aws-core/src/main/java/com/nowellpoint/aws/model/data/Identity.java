package com.nowellpoint.aws.model.data;

import java.util.Date;
import com.nowellpoint.aws.model.annotation.Handler;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Handler(queueName="MONGODB_IDENTITY_COLLECTION_QUEUE")
public class Identity extends AbstractDocument {

	/**
	 * 
	 */

	private static final long serialVersionUID = 3163086585922281575L;

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

	private String fax;

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

	private String timeZoneSidKey;

	/**
	 * 
	 */

	private String localeSidKey;

	/**
	 * 
	 */

	private String emailEncodingKey;

	/**
	 * 
	 */

	private String languageLocaleKey;

	/**
	 * 
	 */

	@JsonSerialize(using = DateSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	private Date lastLoginDate;
	
	/**
	 * 
	 */
	
	private Address address;

	/**
	 * 
	 */

	private String href;

	public Identity() {

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

	public String getLanguageLocaleKey() {
		return languageLocaleKey;
	}

	public void setLanguageLocaleKey(String languageLocaleKey) {
		this.languageLocaleKey = languageLocaleKey;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
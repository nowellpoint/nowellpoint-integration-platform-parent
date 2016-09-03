package com.nowellpoint.api.model.document;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.model.codec.AccountProfileCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.MongoDocument;
import com.nowellpoint.aws.data.mongodb.DateDeserializer;
import com.nowellpoint.aws.data.mongodb.DateSerializer;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="account.profiles", codec=AccountProfileCodec.class)
public class AccountProfileDocument extends MongoDocument {

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
	
	private String languageSidKey;

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

	/**
	 * 
	 */
	
	private Photos photos;
	
	/**
	 * 
	 */
	
	private String leadId;
	
	/**
	 * 
	 */
	
	private Set<CreditCard> creditCards;
	
	/**
	 * 
	 */
	
	private Set<SystemReference> systemReferences;
	
	/**
	 * 
	 */
	
	private Boolean hasFullAccess;
	
	public AccountProfileDocument() {
		setHasFullAccess(Boolean.FALSE);
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

	public String getLanguageSidKey() {
		return languageSidKey;
	}

	public void setLanguageSidKey(String languageSidKey) {
		this.languageSidKey = languageSidKey;
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

	public Photos getPhotos() {
		return photos;
	}

	public void setPhotos(Photos photos) {
		this.photos = photos;
	}

	public String getLeadId() {
		return leadId;
	}

	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}

	public Set<CreditCard> getCreditCards() {
		return creditCards;
	}

	public void setCreditCards(Set<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}

	public Set<SystemReference> getSystemReferences() {
		return systemReferences;
	}

	public void setSystemReferences(Set<SystemReference> systemReferences) {
		this.systemReferences = systemReferences;
	}

	public Boolean getHasFullAccess() {
		return hasFullAccess;
	}

	public void setHasFullAccess(Boolean hasFullAccess) {
		this.hasFullAccess = hasFullAccess;
	}
}
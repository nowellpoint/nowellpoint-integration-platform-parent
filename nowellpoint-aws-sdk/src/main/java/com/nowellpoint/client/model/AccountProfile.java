package com.nowellpoint.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountProfile extends AbstractResource {
	
	private String username;

	private String lastName;

	private String firstName;

	private String name;
	
	private String company;

	private String division;

	private String department;

	private String title;

	private String email;

	private String phone;

	private String extension;

	private String mobilePhone;
	
	private Boolean isActive;

	private String timeZoneSidKey;

	private String localeSidKey;
	
	private String languageSidKey;

	private String emailEncodingKey;

	private Date lastLoginDate;

	private Address address;
	
	private Photos photos;

	private Subscription subscription;
	
	private List<CreditCard> creditCards;
	
	private List<Transaction> transactions;

	private CreditCard primaryCreditCard;

	private Boolean hasFullAccess;

	private Boolean enableSalesforceLogin;

	public AccountProfile() {
		address = new Address();
		photos = new Photos();
		subscription = new Subscription();
		creditCards = new ArrayList<CreditCard>();
		transactions = new ArrayList<Transaction>();
	}
	
	public AccountProfile(String id) {
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

	public String getLanguageSidKey() {
		return languageSidKey;
	}

	public void setLanguageSidKey(String languageSidKey) {
		this.languageSidKey = languageSidKey;
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

	public Photos getPhotos() {
		return photos;
	}

	public void setPhotos(Photos photos) {
		this.photos = photos;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public List<CreditCard> getCreditCards() {
		return creditCards;
	}

	public void setCreditCards(List<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}
	
	public void addCreditCard(CreditCard creditCard) {
		this.getCreditCards().add(creditCard);
	}
	
	public CreditCard getPrimaryCreditCard() {
		return primaryCreditCard;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public void setPrimaryCreditCard(CreditCard primaryCreditCard) {
		this.primaryCreditCard = primaryCreditCard;
	}

	public Boolean getHasFullAccess() {
		return hasFullAccess;
	}

	public void setHasFullAccess(Boolean hasFullAccess) {
		this.hasFullAccess = hasFullAccess;
	}
	
	public Boolean getEnableSalesforceLogin() {
		return enableSalesforceLogin;
	}

	public void setEnableSalesforceLogin(Boolean enableSalesforceLogin) {
		this.enableSalesforceLogin = enableSalesforceLogin;
	}
	
	public AccountProfile withId(String id) {
		setId(id);
		return this;
	}
	
	public AccountProfile withLastName(String lastName) {
		setLastName(lastName);
		return this;
	}
	
	public AccountProfile withFirstName(String firstName) {
		setFirstName(firstName);
		return this;
	}
	
	public AccountProfile withCompany(String company) {
		setCompany(company);
		return this;
	}
	
	public AccountProfile withDivision(String division) {
		setDivision(division);
		return this;
	}
	
	public AccountProfile withDepartment(String department) {
		setDepartment(department);
		return this;
	}
	
	public AccountProfile withTitle(String title) {
		setTitle(title);
		return this;
	}
	
	public AccountProfile withEmail(String email) {
		setEmail(email);
		return this;
	}
	
	public AccountProfile withPhone(String phone) {
		setPhone(phone);
		return this;
	}
	
	public AccountProfile withExtension(String extension) {
		setExtension(extension);
		return this;
	}
	
	public AccountProfile withMobilePhone(String mobilePhone) {
		setMobilePhone(mobilePhone);
		return this;
	}
	
	public AccountProfile withLanguageSidKey(String languageSidKey) {
		setLanguageSidKey(languageSidKey);
		return this;
	}
	
	public AccountProfile withLocaleSidKey(String localeSidKey) {
		setLocaleSidKey(localeSidKey);
		return this;
	}
	
	public AccountProfile withTimeZoneSidKey(String timeZoneSidKey) {
		setTimeZoneSidKey(timeZoneSidKey);
		return this;
	}

	public AccountProfile withEnableSalesforceLogin(Boolean enableSalesforceLogin) {
		setEnableSalesforceLogin(enableSalesforceLogin);
		return this;
	}
}
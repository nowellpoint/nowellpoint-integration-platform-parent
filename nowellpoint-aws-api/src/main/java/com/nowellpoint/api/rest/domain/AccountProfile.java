package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.Photos;
import com.nowellpoint.mongodb.document.MongoDocument;

public class AccountProfile extends AbstractResource {

	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;

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
	
	private String languageSidKey;

	private String localeSidKey;

	private String emailEncodingKey;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date lastLoginDate;
	
	private Address address;
	
	private Subscription subscription;
	
	@JsonIgnore
	private Set<ReferenceLink> referenceLinks;

	@JsonIgnore
	private String accountHref;
	
	@JsonIgnore
	private String emailVerificationToken;
	
	@JsonIgnore
	private Boolean isPasswordVerified;
	
	private Organization organization;
	
	private Photos photos;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Set<CreditCard> creditCards;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Set<Transaction> transactions;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean hasFullAccess;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean enableSalesforceLogin;
	
	private AccountProfile() {
		
	}
	
	private AccountProfile(String id) {
		setId(id);
	}
	
	private <T> AccountProfile(T document) {
		modelMapper.map(document, this);
	}
	
	private AccountProfile(String firstName, String lastName, String email, String organizationId, String countryCode) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.organization = Organization.of(organizationId);
		this.address = Address.of(countryCode);
		this.isPasswordVerified = Boolean.FALSE;
	}

	public static AccountProfile createAccountProfile() {
		return new AccountProfile();
	}
	
	public static AccountProfile of(String firstName, String lastName, String email, String organizationId, String countryCode) {
		return new AccountProfile(firstName, lastName, email, organizationId, countryCode);
	}
	
	public static AccountProfile of(String id) {
		return new AccountProfile(id);
	}
	
	public static AccountProfile of(MongoDocument document) {
		return new AccountProfile(document);
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
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

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Set<ReferenceLink> getReferenceLinks() {
		return referenceLinks;
	}

	public void setReferenceLinks(Set<ReferenceLink> referenceLinks) {
		this.referenceLinks = referenceLinks;
	}

	public String getAccountHref() {
		return accountHref;
	}

	public void setAccountHref(String accountHref) {
		this.accountHref = accountHref;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public Boolean getIsPasswordVerified() {
		return isPasswordVerified;
	}

	public void setIsPasswordVerified(Boolean isPasswordVerified) {
		this.isPasswordVerified = isPasswordVerified;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Photos getPhotos() {
		return photos;
	}

	public void setPhotos(Photos photos) {
		this.photos = photos;
	}	
	
	public Set<CreditCard> getCreditCards() {
		return creditCards;
	}

	public void setCreditCards(Set<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
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
	
	public CreditCard getPrimaryCreditCard() {
		if (creditCards == null || creditCards.isEmpty()) {
			return null;
		}
		return creditCards.stream().filter(c -> c.getPrimary()).findFirst().get();
	}
	
	public void addCreditCard(CreditCard creditCard) {
		if (creditCards == null) {
			creditCards = new HashSet<>();
		}
		if (creditCards.contains(creditCard)) {
			creditCards.remove(creditCard);
		}
		creditCards.add(creditCard);
	}
	
	public void addTransaction(Transaction transaction) {
		if (transactions == null) {
			transactions = new HashSet<>();
		}
		if (transactions.contains(transaction)) {
			transactions.remove(transaction);
		}
		transactions.add(transaction);
	}
	
	public void addReferenceLink(ReferenceLink referenceLink) {
		if (referenceLinks == null) {
			referenceLinks = new HashSet<>();
		}
		if (referenceLinks.contains(referenceLink)) {
			referenceLinks.remove(referenceLink);
		}
		referenceLinks.add(referenceLink);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.AccountProfile.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
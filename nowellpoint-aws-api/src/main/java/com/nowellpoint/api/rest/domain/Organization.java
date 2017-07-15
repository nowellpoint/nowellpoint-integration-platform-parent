package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.Contact;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.document.MongoDocument;

public class Organization extends AbstractResource {

	private Meta meta;
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;	
	
	private String number;
	
	private String name;
	
	private String domain;
	
	private Contact billingContact;
	
	private Address billingAddress;
	
	private Subscription subscription;
	
	private Set<CreditCard> creditCards = new HashSet<>();
	
	private Set<Transaction> transactions = new HashSet<>();
	
	public Organization() {
		
	}
	
	private Organization(String id) {
		this.id = id;
	}
	
	private <T> Organization(T document) {
		modelMapper.map(document, this);
	}
	
	public static Organization of(MongoDocument document) {
		return new Organization(document);
	}
	
	public static Organization of(String id) {
		return new Organization(id);
	}
	
	public static Organization createOrganization(String domain) {
		Organization organization = new Organization();
		organization.setDomain(domain);
		organization.setCreatedBy(UserInfo.of(UserContext.getPrincipal().getName()));
		organization.setCreatedOn(Date.from(Instant.now()));
		organization.setLastUpdatedBy(UserInfo.of(UserContext.getPrincipal().getName()));
		organization.setLastUpdatedOn(Date.from(Instant.now()));
		return organization;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Contact getBillingContact() {
		return billingContact;
	}

	public void setBillingContact(Contact billingContact) {
		this.billingContact = billingContact;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
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

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Organization.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
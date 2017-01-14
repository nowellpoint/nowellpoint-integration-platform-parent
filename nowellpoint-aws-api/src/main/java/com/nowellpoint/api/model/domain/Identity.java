package com.nowellpoint.api.model.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nowellpoint.api.model.document.Photos;

public class Identity extends AccountProfile {
	
	private Subscription subscription;

	@JsonIgnore
	private String accountHref;
	
	@JsonIgnore
	private String emailVerificationToken;
	
	private Photos photos;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Set<CreditCard> creditCards;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Set<Transaction> transactions;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean hasFullAccess;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean enableSalesforceLogin;
	
	public Identity() {
		
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
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

	public void addCreditCard(CreditCard creditCard) {
		if (this.getCreditCards() == null) {
			this.creditCards = new HashSet<CreditCard>();
		}
		this.creditCards.add(creditCard);
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public void addTransaction(Transaction transaction) {
		if (this.getTransactions() == null) {
			this.transactions = new HashSet<Transaction>();
		} else {
			this.transactions.removeIf(t -> t.getId().equals(transaction.getId()));
		}
		this.transactions.add(transaction);
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
}
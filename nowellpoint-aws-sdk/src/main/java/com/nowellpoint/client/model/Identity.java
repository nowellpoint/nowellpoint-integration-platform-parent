package com.nowellpoint.client.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Identity extends AccountProfile {
	
	private Subscription subscription;
	
	private List<CreditCard> creditCards;
	
	private List<Transaction> transactions;

	private CreditCard primaryCreditCard;

	private Boolean hasFullAccess;

	private Boolean enableSalesforceLogin;

	public Identity() {
		subscription = new Subscription();
		creditCards = new ArrayList<CreditCard>();
		transactions = new ArrayList<Transaction>();
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
}
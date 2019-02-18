package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
	
	private static final long serialVersionUID = 865112897491917794L;

	private String id;
	
	private Double amount;
	
	private String currencyIsoCode;
	
	private String status;
	
	private String plan;
	
	private String firstName;
	
	private String lastName;
	
	private Address billingAddress;
	
	private CreditCard creditCard;
	
	private Date billingPeriodStartDate;
	
	private Date billingPeriodEndDate;
	
	private Date createdOn;
	
	private Date updatedOn;
	
	public Transaction() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrencyIsoCode() {
		return currencyIsoCode;
	}

	public void setCurrencyIsoCode(String currencyIsoCode) {
		this.currencyIsoCode = currencyIsoCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Date getBillingPeriodStartDate() {
		return billingPeriodStartDate;
	}

	public void setBillingPeriodStartDate(Date billingPeriodStartDate) {
		this.billingPeriodStartDate = billingPeriodStartDate;
	}

	public Date getBillingPeriodEndDate() {
		return billingPeriodEndDate;
	}

	public void setBillingPeriodEndDate(Date billingPeriodEndDate) {
		this.billingPeriodEndDate = billingPeriodEndDate;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

}
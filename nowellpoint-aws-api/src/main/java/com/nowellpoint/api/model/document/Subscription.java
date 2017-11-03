
/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.api.model.document;

import java.io.Serializable;
import java.util.Date;

import com.nowellpoint.mongodb.annotation.EmbedOne;

public class Subscription implements Serializable {

	private static final long serialVersionUID = -7569793449815113870L;
	
	private String number;
	
	private String planId;
	
	private String planName;
	
	private String planCode;
	
	private String currencyIsoCode;
	
	private String currencySymbol;
	
	private Double unitPrice;
	
	private String billingFrequency;
	
	private String status;
	
	private Date nextBillingDate;
	
	private Date billingPeriodStartDate;
	
	private Date billingPeriodEndDate;
	
	private Date addedOn;
	
	private Date updatedOn;
	
	@EmbedOne
	private Contact billingContact;
	
	@EmbedOne
	private Address billingAddress;
	
	@EmbedOne
	private CreditCard creditCard;
	
	public Subscription() {
		
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPlanId() {
		return planId;
	}
	
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	
	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getCurrencyIsoCode() {
		return currencyIsoCode;
	}

	public void setCurrencyIsoCode(String currencyIsoCode) {
		this.currencyIsoCode = currencyIsoCode;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getNextBillingDate() {
		return nextBillingDate;
	}

	public void setNextBillingDate(Date nextBillingDate) {
		this.nextBillingDate = nextBillingDate;
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

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}
}
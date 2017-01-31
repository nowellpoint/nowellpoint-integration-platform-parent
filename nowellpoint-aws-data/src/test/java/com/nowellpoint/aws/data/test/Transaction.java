package com.nowellpoint.aws.data.test;

import java.io.Serializable;
import java.util.Date;

import com.nowellpoint.mongodb.annotation.EmbedOne;

public class Transaction implements Serializable {

	private static final long serialVersionUID = 872388774509924244L;
	
	private String id;
	
	private Double amount;
	
	private String currencyIsoCode;
	
	private String status;
	
	@EmbedOne
	private CreditCard creditCard;
	
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

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
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
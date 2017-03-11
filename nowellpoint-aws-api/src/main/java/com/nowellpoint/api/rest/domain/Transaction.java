package com.nowellpoint.api.rest.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Transaction {
	
	private String id;
	
	private Double amount;
	
	private String currencyIsoCode;
	
	private String status;
	
	private CreditCard creditCard;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date createdOn;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
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
	
	@Override
	public String toString() {
		return null;
	}

	@Override
	public int hashCode() {
		return 0; //Objects.hashCode(this.id);
	}

	@Override
	public boolean equals(Object obj) {
//		if (obj instanceof CreditCard) {
//			Transaction other = (Transaction) obj;
//			return Objects.equal(this.id, other.id);
//		}
		return false;
	}
}
package com.nowellpoint.client.model;

import java.util.Date;

public class CreditCard {
	
	private String cardType;
	
	private String cardholderName;
	
	private String cvv;
	
	private String number;
	
	private String expirationMonth;
	
	private String expirationYear;
	
	private String lastFour;
	
	private String token;
	
	private String imageUrl;
	
	private Address billingAddress;
	
	private Contact billingContact;
	
	private Boolean primary;
	
	private Date addedOn;
	
	private Date updatedOn;
	
	public CreditCard() {
		
	}

	public String getCardType() {
		return cardType;
	}

	public String getCardholderName() {
		return cardholderName;
	}

	public String getCvv() {
		return cvv;
	}

	public String getNumber() {
		return number;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	public String getLastFour() {
		return lastFour;
	}

	public String getToken() {
		return token;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public Contact getBillingContact() {
		return billingContact;
	}
	
	public Boolean getPrimary() {
		return primary;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}
}
package com.nowellpoint.api.rest.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.Contact;

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

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date addedOn;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date updatedOn;
	
	public CreditCard() {
		
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	
	public String getCardholderName() {
		return cardholderName;
	}

	public void setCardholderName(String cardholderName) {
		this.cardholderName = cardholderName;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLastFour() {
		return lastFour;
	}

	public void setLastFour(String lastFour) {
		this.lastFour = lastFour;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Contact getBillingContact() {
		return billingContact;
	}

	public void setBillingContact(Contact billingContact) {
		this.billingContact = billingContact;
	}

	public Boolean getPrimary() {
		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
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

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("token", token)
				.add("cardType", cardType)
				.add("cardholderName", cardholderName)
				.add("lastFour", lastFour)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.token);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CreditCard) {
			CreditCard other = (CreditCard) obj;
			return Objects.equal(this.token, other.token);
		}
		return false;
	}
}
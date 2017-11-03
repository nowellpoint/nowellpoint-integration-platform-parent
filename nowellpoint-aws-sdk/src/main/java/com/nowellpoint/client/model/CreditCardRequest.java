package com.nowellpoint.client.model;

public class CreditCardRequest {
	
	private String organizationId;
	
	private String token;
	
	private String cardholderName;
	
	private String number;
	
	private String expirationMonth;
	
	private String expirationYear;
	
	private String firstName;
	
	private String lastName;
	
	private String cvv;
	
	private Boolean primary;
	
	private String street;

	private String city;

	private String state;

	private String postalCode;

	private String countryCode;
	
	public CreditCardRequest() {
		
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCardholderName() {
		return cardholderName;
	}

	public void setCardholderName(String cardholderName) {
		this.cardholderName = cardholderName;
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

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public Boolean getPrimary() {
		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public CreditCardRequest withOrganizationId(String organizationId) {
		setOrganizationId(organizationId);
		return this;
	}
	
	public CreditCardRequest withCardholderName(String cardholderName) {
		setCardholderName(cardholderName);
		return this;
	}
	
	public CreditCardRequest withToken(String token) {
		setToken(token);
		return this;
	}

	public CreditCardRequest withNumber(String number) {
		setNumber(number);
		return this;
	}
	
	public CreditCardRequest withCvv(String cvv) {
		setCvv(cvv);
		return this;
	}
	
	public CreditCardRequest withExpirationMonth(String expirationMonth) {
		setExpirationMonth(expirationMonth);
		return this;
	}
	
	public CreditCardRequest withExpirationYear(String expirationYear) {
		setExpirationYear(expirationYear);
		return this;
	}

	public CreditCardRequest withPrimary(Boolean primary) {
		setPrimary(primary);
		return this;
	}
	
	public CreditCardRequest withStreet(String street) {
		setStreet(street);
		return this;
	}
	
	public CreditCardRequest withCity(String city) {
		setCity(city);
		return this;
	}
	
	public CreditCardRequest withState(String state) {
		setState(state);
		return this;
	}
	
	public CreditCardRequest withPostalCode(String postalCode) {
		setPostalCode(postalCode);
		return this;
	}
	
	public CreditCardRequest withCountryCode(String countryCode) {
		setCountryCode(countryCode);
		return this;
	}
	
	public CreditCardRequest withFirstName(String firstName) {
		setFirstName(firstName);
		return this;
	}
	
	public CreditCardRequest withLastName(String lastName) {
		setLastName(lastName);
		return this;
	}
}
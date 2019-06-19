package com.nowellpoint.client.sforce.model;

import com.nowellpoint.client.sforce.annotation.Column;
import com.nowellpoint.client.sforce.annotation.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Account extends SObject {
	
	private static final long serialVersionUID = 1402870861788633127L;
	
	@Getter @Column(name="AccountNumber") private String accountNumber;
	@Getter @Column(name="AccountSource") private String accountSource;
	@Getter @Column(name="AnnualRevenue") private Double annualRevenue;
	@Getter @Column(name="BillingStreet") private String billingStreet;
	@Getter @Column(name="BillingCity") private String billingCity;
	@Getter @Column(name="BillingState") private String billingState;
	@Getter @Column(name="BillingStateCode") private String billingStateCode;
	@Getter @Column(name="BillingCountry") private String billingCountry;
	@Getter @Column(name="BillingCountryCode") private String billingCountryCode;
	@Getter @Column(name="BillingPostalCode") private String billingPostalCode;
	@Getter @Column(name="BillingLongitude") private String billingLongitude;
	@Getter @Column(name="BillingLatitude") private String billingLatitude;
	@Getter @Column(name="Description") private String description;
	@Getter @Column(name="Industry") private String industry;
	@Getter @Column(name="NumberOfEmployees") private Integer numberOfEmployees;
	@Getter @Column(name="Owner") private UserInfo owner;
	@Getter @Column(name="Ownership") private String ownership;
	@Getter @Column(name="Phone") private String phone;
	@Getter @Column(name="Rating") private String rating;
	@Getter @Column(name="ShippingStreet") private String shippingStreet;
	@Getter @Column(name="ShippingCity") private String shippingCity;
	@Getter @Column(name="ShippingState") private String shippingState;
	@Getter @Column(name="ShippingStateCode") private String shippingStateCode;
	@Getter @Column(name="ShippingCountry") private String shippingCountry;
	@Getter @Column(name="ShippingCountryCode") private String shippingCountryCode;
	@Getter @Column(name="ShippingPostalCode") private String shippingPostalCode;
	@Getter @Column(name="ShippingLongitude") private String shippingLongitude;
	@Getter @Column(name="ShippingLatitude") private String shippingLatitude;
	@Getter @Column(name="Sic") private String sic;
	@Getter @Column(name="SicDesc") private String sicDesc;
	@Getter @Column(name="Site") private String site;
	@Getter @Column(name="TickerSymbol") private String tickerSymbol;
	@Getter @Column(name="Type") private String type;
	@Getter @Column(name="Website") private String website;
}
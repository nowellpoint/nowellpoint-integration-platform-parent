package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account extends SObject {
	
	private static final long serialVersionUID = 1402870861788633127L;
	
	public static final String QUERY = SOBJECT_QUERY
			+ ", "
			+ "BillingStreet, "
			+ "BillingCity, "
			+ "BillingState, "
			+ "BillingCountryCode, "
			+ "BillingCountry, "
			+ "BillingPostalCode, "
			+ "BillingLongitude, "
			+ "BillingLatitude, "
			+ "ShippingStreet, "
			+ "ShippingCity, "
			+ "ShippingState, "
			+ "ShippingCountryCode, "
			+ "ShippingCountry, "
			+ "ShippingPostalCode, "
			+ "ShippingLongitude, "
			+ "ShippingLatitude, "
			+ "TickerSymbol, "
			+ "Website "
			+ "From Account ";
	
	@Getter @JsonProperty(value="BillingStreet") private String billingStreet;
	@Getter @JsonProperty(value="BillingCity") private String billingCity;
	@Getter @JsonProperty(value="BillingState") private String billingState;
	@Getter @JsonProperty(value="BillingStateCode") private String billingStateCode;
	@Getter @JsonProperty(value="BillingCountry") private String billingCountry;
	@Getter @JsonProperty(value="BillingCountryCode") private String billingCountryCode;
	@Getter @JsonProperty(value="BillingPostalCode") private String billingPostalCode;
	@Getter @JsonProperty(value="BillingLongitude")private String billingLongitude;
	@Getter @JsonProperty(value="BillingLatitude") private String billingLatitude;
	@Getter @JsonProperty(value="ShippingStreet") private String shippingStreet;
	@Getter @JsonProperty(value="ShippingCity") private String shippingCity;
	@Getter @JsonProperty(value="ShippingState") private String shippingState;
	@Getter @JsonProperty(value="ShippingStateCode") private String shippingStateCode;
	@Getter @JsonProperty(value="ShippingCountry") private String shippingCountry;
	@Getter @JsonProperty(value="ShippingCountryCode") private String shippingCountryCode;
	@Getter @JsonProperty(value="ShippingPostalCode") private String shippingPostalCode;
	@Getter @JsonProperty(value="ShippingLongitude")private String shippingLongitude;
	@Getter @JsonProperty(value="ShippingLatitude") private String shippingLatitude;
	@Getter @JsonProperty(value="TickerSymbol") private String tickerSymbol;
	@Getter @JsonProperty(value="Website") private String website;

	public Account() { }
}
package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Serializable {
	
	private static final long serialVersionUID = 3317676775699599582L;
	@Getter @JsonProperty(value="street") private String street;
	@Getter @JsonProperty(value="city") private String city;
	@Getter @JsonProperty(value="state") private String state;
	@Getter @JsonProperty(value="stateCode") private String stateCode;
	@Getter @JsonProperty(value="country") private String country;
	@Getter @JsonProperty(value="countryCode") private String countryCode;
	@Getter @JsonProperty(value="postalCode") private String postalCode;
	@Getter @JsonProperty(value="longitude") private Double longitude;
	@Getter @JsonProperty(value="latitude") private Double latitude;
}
package com.nowellpoint.listener.model;

import javax.json.bind.annotation.JsonbProperty;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class Address {
	@Getter @JsonbProperty("Street") private String street;
	@Getter @JsonbProperty("City") private String city;
	@Getter @JsonbProperty("State") private String state;
	@Getter @JsonbProperty("StateCode") private String stateCode;
	@Getter @JsonbProperty("PostalCode") private String postalCode;
	@Getter @JsonbProperty("Country") private String country;
	@Getter @JsonbProperty("CountryCode") private String countryCode;
	@Getter @JsonbProperty("Latitude") private String latitude;
	@Getter @JsonbProperty("Longitude") private String longitude;
	
	@BsonCreator
	public Address(@BsonProperty("street") String street,
			@BsonProperty("city") String city,
			@BsonProperty("state") String state,
			@BsonProperty("stateCode") String stateCode,
			@BsonProperty("postalCode") String postalCode,
			@BsonProperty("country") String country,
			@BsonProperty("countryCode") String countryCode,
			@BsonProperty("latitude") String latitude,
			@BsonProperty("longitude") String longitude) {
		
		this.street = street;
		this.city = city;
		this.state = state;
		this.stateCode = stateCode;
		this.postalCode = postalCode;
		this.country = country;
		this.countryCode = countryCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}
}
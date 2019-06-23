package com.nowellpoint.listener.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Builder
@Getter
@NoArgsConstructor
public class Address {
	private String street;
	private String city;
	private String state;
	private String stateCode;
	private String postalCode;
	private String country;
	private String countryCode;
	private Double latitude;
	private Double longitude;
	private String addressId;
	
	@BsonCreator
	public Address(@BsonProperty("street") String street,
			@BsonProperty("city") String city,
			@BsonProperty("state") String state,
			@BsonProperty("stateCode") String stateCode,
			@BsonProperty("postalCode") String postalCode,
			@BsonProperty("country") String country,
			@BsonProperty("countryCode") String countryCode,
			@BsonProperty("latitude") Double latitude,
			@BsonProperty("longitude") Double longitude,
			@BsonProperty("addressId") String addressId) {
		
		this.street = street;
		this.city = city;
		this.state = state;
		this.stateCode = stateCode;
		this.postalCode = postalCode;
		this.country = country;
		this.countryCode = countryCode;
		this.latitude = latitude;
		this.longitude = longitude;
		this.addressId = addressId;
	}
}
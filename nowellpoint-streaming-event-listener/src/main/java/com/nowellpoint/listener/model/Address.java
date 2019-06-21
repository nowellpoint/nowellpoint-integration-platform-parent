package com.nowellpoint.listener.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class Address {
	@Getter private String street;
	@Getter private String city;
	@Getter private String state;
	@Getter private String stateCode;
	@Getter private String postalCode;
	@Getter private String country;
	@Getter private String countryCode;
	@Getter private Double latitude;
	@Getter private Double longitude;
	
	@BsonCreator
	public Address(@BsonProperty("street") String street,
			@BsonProperty("city") String city,
			@BsonProperty("state") String state,
			@BsonProperty("stateCode") String stateCode,
			@BsonProperty("postalCode") String postalCode,
			@BsonProperty("country") String country,
			@BsonProperty("countryCode") String countryCode,
			@BsonProperty("latitude") Double latitude,
			@BsonProperty("longitude") Double longitude) {
		
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
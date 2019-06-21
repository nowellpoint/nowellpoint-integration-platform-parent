package com.nowellpoint.listener.model;

import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class GeoCodedAddress {
	@Getter private String street;
	@Getter private String city;
	@Getter private String state;
	@Getter private String stateCode;
	@Getter private String postalCode;
	@Getter private String country;
	@Getter private String countryCode;
	@Getter private Double latitude;
	@Getter private Double longitude;
	@Getter private String formattedAddress;
	@Getter private Boolean partialMatch;
	@Getter private String placeId;
	@Getter private String plusCode;
	
	public GeoCodedAddress(@BsonProperty("street") String street,
			@BsonProperty("city") String city,
			@BsonProperty("state") String state,
			@BsonProperty("stateCode") String stateCode,
			@BsonProperty("postalCode") String postalCode,
			@BsonProperty("country") String country,
			@BsonProperty("countryCode") String countryCode,
			@BsonProperty("latitude") Double latitude,
			@BsonProperty("longitude") Double longitude,
			@BsonProperty("formattedAddress") String formattedAddress,
			@BsonProperty("partialMatch") Boolean partialMatch,
			@BsonProperty("placeId") String placeId,
			@BsonProperty("plusCode") String plusCode) {
		
		this.street = street;
		this.city = city;
		this.state = state;
		this.stateCode = stateCode;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
		this.formattedAddress = formattedAddress;
		this.partialMatch = partialMatch;
		this.placeId = placeId;
		this.plusCode = plusCode;
	}
}
package com.nowellpoint.listener.model;

import java.util.Date;
import java.util.List;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Builder
@Getter
@NoArgsConstructor
public class GeoCodedAddress {
	private @BsonId String id;
	private Double latitude;
	private Double longitude;
	private String formattedAddress;
	private Boolean partialMatch;
	private String compoundCode;
	private String globalCode;
	private List<AddressComponent> addressComponents;
	private @Builder.Default Date verifiedOn = Date.from(Instant.now());
	
	public GeoCodedAddress(@BsonId String id,
			@BsonProperty("latitude") Double latitude,
			@BsonProperty("longitude") Double longitude,
			@BsonProperty("formattedAddress") String formattedAddress,
			@BsonProperty("partialMatch") Boolean partialMatch,
			@BsonProperty("globalCode") String globalCode,
			@BsonProperty("compoundCode") String compoundCode,
			@BsonProperty("addressComponents") List<AddressComponent> addressComponents,
			@BsonProperty("verifiedOn") Date verifiedOn) {
		
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.formattedAddress = formattedAddress;
		this.partialMatch = partialMatch;
		this.globalCode = globalCode;
		this.compoundCode = compoundCode;
		this.addressComponents = addressComponents;
		this.verifiedOn = verifiedOn;
	}
}
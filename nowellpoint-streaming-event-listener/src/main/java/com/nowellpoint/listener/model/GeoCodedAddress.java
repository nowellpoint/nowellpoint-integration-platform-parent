package com.nowellpoint.listener.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.jboss.logging.Logger;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Builder
@Getter
@NoArgsConstructor
public class GeoCodedAddress {
	private static final Logger LOGGER = Logger.getLogger(GeoCodedAddress.class);
	
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
	
	public static GeoCodedAddress of(Address address) {
		if (address == null)
			return null;
		
		String addressString = new StringBuilder()
				.append(address.getStreet())
				.append(" ")
				.append(address.getCity())
				.append(", ")
				.append(address.getState())
				.append(" ")
				.append(address.getPostalCode())
				.append(" ")
				.append(address.getCountryCode())
				.toString();
		
		GeoApiContext context = new GeoApiContext.Builder()
				.apiKey(System.getenv("GOOGLE_API_KEY"))
			    .build();
		
		try {
			GeocodingResult[] result = GeocodingApi.geocode(context, addressString).await();
			
			if (result != null) {
				return GeoCodedAddress.builder()
						.addressComponents(Arrays.stream(result[0].addressComponents)
								.map(ac -> AddressComponent.builder()
										.longName(ac.longName)
										.shortName(ac.shortName)
										.types(Arrays.stream(ac.types)
												.map(t -> t.name())
												.collect(Collectors.toList()))
										.build())
								.collect(Collectors.toList()))
						.formattedAddress(result[0].formattedAddress)
						.latitude(result[0].geometry.location.lat)
						.longitude(result[0].geometry.location.lng)
						.partialMatch(result[0].partialMatch)
						.id(result[0].placeId)
						.globalCode(result[0].plusCode.globalCode)
						.compoundCode(result[0].plusCode.compoundCode)
						.build();
			} else {
				return null;
			}
		} catch (ApiException | InterruptedException | IOException e) {
			LOGGER.warn(e);
			return null;
		}
	}
}
package com.nowellpoint.listener.model;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
public class AddressComponent {
	private String longName;
	private String shortName;
	private List<String> types;
	
	@BsonCreator
	public AddressComponent(@BsonProperty("longName") String longName,
			@BsonProperty("shortName") String shortName,
			@BsonProperty("types") List<String> types) {
		
		this.longName = longName;
		this.shortName = shortName;
		this.types = types;
	}
}
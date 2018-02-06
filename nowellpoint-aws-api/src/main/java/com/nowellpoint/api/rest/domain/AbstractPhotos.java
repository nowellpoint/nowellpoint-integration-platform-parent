package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Photos.class)
@JsonDeserialize(as = Photos.class)
public abstract class AbstractPhotos {
	public abstract String getProfilePicture();
	
	public static Photos of(com.nowellpoint.api.model.document.Photos source) {
		Photos photos = Photos.builder()
				.profilePicture(source.getProfilePicture())
				.build();
		
		return photos;
	}
}
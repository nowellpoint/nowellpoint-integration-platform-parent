package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Photos.class)
@JsonDeserialize(as = Photos.class)
public abstract class AbstractPhotos {
	public abstract String getProfilePicture();
	
	public static Photos of(com.nowellpoint.console.entity.Photos source) {
		return source == null ? null : Photos.builder()
				.profilePicture(source.getProfilePicture())
				.build();
	}
}
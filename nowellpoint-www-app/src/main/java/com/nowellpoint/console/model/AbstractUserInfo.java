package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = UserInfo.class)
@JsonDeserialize(as = UserInfo.class)
public abstract class AbstractUserInfo {
	public abstract String getId();
	public abstract String getEmail();
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract Photos getPhotos();
	
	@Value.Derived
	public String getName() {
		return getFirstName() != null ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	public static UserInfo of(com.nowellpoint.console.entity.Identity source) {
		return source == null ? null : UserInfo.builder()
				.id(source.getId().toString())
				.email(source.getEmail())
				.firstName(source.getFirstName())
				.lastName(source.getLastName())
				.photos(Photos.of(source.getPhotos()))
				.build();
	}
	
	public static UserInfo of(Identity source) {
		return source == null ? null : UserInfo.builder()
				.id(source.getId())
				.email(source.getEmail())
				.firstName(source.getFirstName())
				.lastName(source.getLastName())
				.photos(Photos.of(source.getPhotos()))
				.build();
	}
}
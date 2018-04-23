package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.UserProfileResource;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = UserInfo.class)
@JsonDeserialize(as = UserInfo.class)
public abstract class AbstractUserInfo {
	public abstract String getId(); 
	public abstract @Nullable String getLastName();
	public abstract @Nullable String getFirstName();
	public abstract @Nullable String getName();
	public abstract @Nullable String getEmail();
	public abstract @Nullable String getPhone();
	public abstract @Nullable Photos getPhotos();
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(UserProfileResource.class)
				.build();
	}
}
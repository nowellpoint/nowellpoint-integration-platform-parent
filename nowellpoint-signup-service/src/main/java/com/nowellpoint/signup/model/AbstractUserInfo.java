package com.nowellpoint.signup.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = UserInfo.class)
@JsonDeserialize(as = UserInfo.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractUserInfo {
	public abstract @Nullable String getId();
	public abstract String getUsername();
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract @Nullable String getTitle();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract Boolean getIsActive();
	
	@Value.Derived
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
//	public Meta getMeta() {
//		return Meta.builder()
//				.id(getId().toString())
//				//.resourceClass(UserProfile.class)
//				.build();
//	}
}
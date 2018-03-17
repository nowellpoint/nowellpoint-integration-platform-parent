package com.nowellpoint.signup.model;

import java.net.URI;
import java.util.Date;

import javax.annotation.Nullable;

import org.bson.types.ObjectId;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.signup.rest.RegistrationResource;
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Registration.class)
@JsonDeserialize(as = Registration.class)
public abstract class AbstractRegistration {
	public abstract @Nullable String getId();
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract String getCountryCode();
	public abstract String getEmailVerificationToken();
	public abstract @Nullable String getDomain();
	public abstract @Nullable URI getEmailVerificationHref();
	public abstract Long getExpiresAt();
	public abstract String getPlanId();
	public abstract @Nullable String getIdentityHref(); 
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract Boolean getVerified();
	public abstract Date getCreatedOn();
	public abstract Date getLastUpdatedOn();
	
	@Value.Derived
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	public Meta getMeta() {
		return Meta.builder()
				.id(getId().toString())
				.resourceClass(RegistrationResource.class)
				.build();
	}
}
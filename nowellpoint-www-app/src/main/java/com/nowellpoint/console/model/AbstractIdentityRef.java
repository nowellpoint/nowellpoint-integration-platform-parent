package com.nowellpoint.console.model;

import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = UserProfile.class)
@JsonDeserialize(as = UserProfile.class)
public abstract class AbstractIdentityRef {
	public abstract String getId();
	public abstract String getUsername();
	public abstract String getName();
	public abstract String getLastName();
	public abstract String getFirstName();
	public abstract String getTitle();
	public abstract String getEmail();
	public abstract String getPhone();
	public abstract Boolean getIsActive();
	public abstract Address getAddress();
	public abstract Preferences getPreferences();
	public abstract Organization getOrganization();
	public abstract Photos getPhotos();
	public abstract Date getCreatedOn();
	public abstract Date getLastUpdatedOn();
}
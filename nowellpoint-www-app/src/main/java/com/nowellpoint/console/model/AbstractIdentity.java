package com.nowellpoint.console.model;

import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

import org.bson.types.ObjectId;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.IdentityResource;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Identity.class)
@JsonDeserialize(as = Identity.class)
public abstract class AbstractIdentity {
	public abstract String getEmail();
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getSubject();
	public abstract String getStatus();
	public abstract @Nullable OrganizationInfo getOrganization();
	public abstract @Nullable Photos getPhotos();
	
	@Value.Derived
	public String getName() {
		return getFirstName() != null ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(IdentityResource.class)
				.build();
	}
	
	@Value.Default
	public String getId() {
		return new ObjectId().toString();
	}
	
	@Value.Default
	public Boolean getActive() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Locale getLocale() {
		return Locale.getDefault();
	}
	
	@Value.Default
	public String getTimeZone() {
		return TimeZone.getDefault().getID();
	}
	
	@Value.Default
	public String getUsername() {
		return getEmail();
	}
	
	public static Identity of(com.nowellpoint.console.entity.Identity source) {
		return source == null ? null : Identity.builder()
				.id(source.getId().toString())
				.email(source.getEmail())
				.firstName(source.getFirstName())
				.lastName(source.getLastName())
				.locale(source.getLocale())
				.timeZone(source.getTimeZone())
				.subject(source.getSubject())
				.status(source.getStatus())
				.organization(OrganizationInfo.of(source.getOrganization()))
				.photos(Photos.of(source.getPhotos()))
				.build();
	}
}
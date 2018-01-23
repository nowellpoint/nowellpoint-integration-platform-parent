package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.UserProfileResource;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = UserProfile.class)
@JsonDeserialize(as = UserProfile.class)
public abstract class AbstractUserProfile extends AbstractImmutableResource {
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract String getLastName();
	public abstract String getFirstName();
	public abstract @Nullable String getTitle();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract Boolean getIsActive();
	public abstract TimeZone getTimeZone();
	public abstract Locale getLocale();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getLastLoginDate();
	public abstract Address getAddress();
	public abstract @Nullable OrganizationInfo getOrganization();
	public abstract @Nullable Photos getPhotos();
	public abstract @JsonIgnore String getReferenceId();
	
	@Value.Derived
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	@Value.Derived
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(UserProfileResource.class)
				.build();
	}
	
	@Value.Derived
	public String getUsername() {
		return getEmail();
	}
	
	public static UserProfile of(com.nowellpoint.api.model.document.UserProfile source) {
		ModifiableUserProfile userProfile = modelMapper.map(source, ModifiableUserProfile.class);
		return userProfile.toImmutable();
	}
	
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(UserProfile.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.UserProfile.class);
	}
}
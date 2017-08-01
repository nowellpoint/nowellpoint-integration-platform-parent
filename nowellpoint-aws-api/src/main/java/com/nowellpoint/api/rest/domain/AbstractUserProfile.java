package com.nowellpoint.api.rest.domain;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.UserProfileResource;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"referenceLink:referenceLinks"})
@JsonSerialize(as = UserProfile.class)
@JsonDeserialize(as = UserProfile.class)
public abstract class AbstractUserProfile extends AbstractImmutableResource {
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	public abstract String getUsername();
	public abstract String getLastName();
	public abstract String getFirstName();
	public abstract @Nullable String getCompany();
	public abstract @Nullable String getDivision();
	public abstract @Nullable String getDepartment();
	public abstract @Nullable String getTitle();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract @Nullable String getExtension();
	public abstract @Nullable String getMobilePhone();
	public abstract Boolean getIsActive();
	public abstract TimeZone getTimeZone();
	public abstract Locale getLocale();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getLastLoginDate();
	public abstract Address getAddress();
	public abstract @JsonIgnore Set<ReferenceLink> getReferenceLinks();
	public abstract @Nullable OrganizationInfo getOrganization();
	public abstract Photos getPhotos();
	
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	public static UserProfile of(com.nowellpoint.api.model.document.UserProfile source) {
		
		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(UserProfileResource.class)
				.build(source.getId().toString());
				
		Meta meta = Meta.builder()
				.href(href.toString())
				.build();
		
		Address address = Address.builder()
				.city(source.getAddress().getCity())
				.country(source.getAddress().getCountry())
				.countryCode(source.getAddress().getCountryCode())
				.id(source.getAddress().getId())
				.latitude(source.getAddress().getLatitude())
				.longitude(source.getAddress().getLongitude())
				.postalCode(source.getAddress().getPostalCode())
				.state(source.getAddress().getState())
				.stateCode(source.getAddress().getStateCode())
				.street(source.getAddress().getStreet())
				.build();
		
		Photos photos = Photos.builder()
				.profilePicture(source.getPhotos().getProfilePicture())
				.build();
		
		Set<ReferenceLink> referenceLinks = new HashSet<>();
		source.getReferenceLinks().forEach(l -> {
			ReferenceLink referenceLink = ReferenceLink.of(ReferenceLinkTypes.valueOf(l.getName()), l.getId());
			referenceLinks.add(referenceLink);
		});
		
		UserProfile userProfile = UserProfile.builder()
				.id(source.getId().toString())
				.address(address)
				.company(source.getCompany())
				.createdBy(modelMapper.map(source.getCreatedBy(), UserInfo.class))
				.createdOn(source.getCreatedOn())
				.lastUpdatedBy(modelMapper.map(source.getCreatedBy(), UserInfo.class))
				.lastUpdatedOn(source.getLastUpdatedOn())
				.department(source.getDepartment())
				.division(source.getDivision())
				.email(source.getEmail())
				.extension(source.getExtension())
				.firstName(source.getFirstName())
				.isActive(source.getIsActive())
				.lastLoginDate(source.getLastLoginDate())
				.lastName(source.getLastName())
				.locale(source.getLocale())
				.meta(meta)
				.mobilePhone(source.getMobilePhone())
				.organization(modelMapper.map(source.getOrganization(), OrganizationInfo.class))
				.phone(source.getPhone())
				.timeZone(source.getTimeZone())
				.title(source.getTitle())
				.username(source.getUsername())
				.photos(photos)
				.referenceLinks(referenceLinks)
				.build();
		
		return userProfile;
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
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
	public abstract @Nullable Address getAddress();
	public abstract @Nullable Resources getResources();
	
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
		OrganizationInfo organization = OrganizationInfo.of(source.getOrganization());
		
//		String jobsHref = UriBuilder.fromUri("https://localhost:8443")
//				//.path(JobResource.class)
//				.build()
//				.toString();
//		
//		String connectorsHref = UriBuilder.fromUri("https://localhost:8443")
//				//.path(ConnectorResource.class)
//				.build()
//				.toString();
		
		Identity identity = Identity.builder()
				//.address(Address.of(source.getUserProfile().getAddress()))
				.id(source.getId().toString())
				.email(source.getEmail())
				.firstName(source.getFirstName())
				.lastName(source.getLastName())
				//.locale(source.getLocale())
				//.timeZone(source.getUserProfile().getPreferences().getTimeZone())
				.organization(organization)
				.subject(source.getSubject())
				.status(source.getStatus())
//				.resources(Resources.builder()
//						.userProfile(userInfo.getMeta().getHref())
//						.connectors(connectorsHref)
//						.organization(organization.getMeta().getHref())
//						.jobs(jobsHref)
//						.build())
				.build();
		
		return identity;
	}
}
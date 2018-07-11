package com.nowellpoint.console.model;

import java.util.Locale;

import javax.ws.rs.core.UriBuilder;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.IdentityResource;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Identity.class)
@JsonDeserialize(as = Identity.class)
public abstract class AbstractIdentity {
	public abstract String getId();
	public abstract String getUserId();
	public abstract String getEmail();
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getName();
	public abstract OrganizationInfo getOrganization();
	public abstract String getTimeZone();
	public abstract Locale getLocale();
	public abstract Address getAddress();
	public abstract Resources getResources();
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(IdentityResource.class)
				.build();
	}
	
	public static Identity of(com.nowellpoint.console.entity.Identity source) {
		OrganizationInfo organization = OrganizationInfo.of(source.getOrganization());
		
		UserInfo userInfo = UserInfo.of(source.getUserProfile());
		
		String jobsHref = UriBuilder.fromUri("https://localhost:8443")
				//.path(JobResource.class)
				.build()
				.toString();
		
		String connectorsHref = UriBuilder.fromUri("https://localhost:8443")
				//.path(ConnectorResource.class)
				.build()
				.toString();
		
		Identity identity = Identity.builder()
				.address(Address.of(source.getUserProfile().getAddress()))
				.id(source.getId().toString())
				.userId(source.getUserProfile().getId().toString())
				.email(source.getUserProfile().getEmail())
				.firstName(source.getUserProfile().getFirstName())
				.lastName(source.getUserProfile().getLastName())
				.name(source.getUserProfile().getName())
				.locale(source.getUserProfile().getPreferences().getLocale())
				.timeZone(source.getUserProfile().getPreferences().getTimeZone())
				.organization(organization)
				.resources(Resources.builder()
						.userProfile(userInfo.getMeta().getHref())
						.connectors(connectorsHref)
						.organization(organization.getMeta().getHref())
						.jobs(jobsHref)
						.build())
				.build();
		
		return identity;
	}
}
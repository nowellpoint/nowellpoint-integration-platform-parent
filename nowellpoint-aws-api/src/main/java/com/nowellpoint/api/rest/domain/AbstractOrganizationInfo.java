package com.nowellpoint.api.rest.domain;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = OrganizationInfo.class)
@JsonDeserialize(as = OrganizationInfo.class)
public abstract class AbstractOrganizationInfo {
	public abstract String getId();
	public abstract @Nullable UserInfo getCreatedBy();
	public abstract @Nullable UserInfo getLastUpdatedBy();	
	public abstract @Nullable String getNumber();
	public abstract @Nullable String getDomain();
	public abstract @Nullable Subscription getSubscription();
	
	public Meta getMeta() {
		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(OrganizationResource.class)
				.path("/{id}")
				.build(Assert.isNotNullOrEmpty(getId()) ? getId() : "{id}");
				
		Meta meta = Meta.builder()
				.href(href.toString())
				.build();
		
		return meta;
	}
	
	public static OrganizationInfo of(String id) {
		Assert.assertNotNullOrEmpty(id, "Organization Id cannot be null or empty");
		ModifiableOrganizationInfo organizationInfo = ModifiableOrganizationInfo.create().setId(id);
		return organizationInfo.toImmutable();
	}	
}
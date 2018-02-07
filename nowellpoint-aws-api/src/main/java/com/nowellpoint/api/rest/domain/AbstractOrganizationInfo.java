package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.util.Assert;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

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
	
	@Value.Derived
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(OrganizationResource.class)
				.build();
	}
	
	public static OrganizationInfo of(String id) {
		Assert.assertNotNullOrEmpty(id, "Organization Id cannot be null or empty");
		ModifiableOrganizationInfo organizationInfo = ModifiableOrganizationInfo.create().setId(id);
		return organizationInfo.toImmutable();
	}	
	
	public static OrganizationInfo of(Jws<Claims> claims) {
		Assert.assertNotNull(claims, "Jws claims cannot be null");
		ModifiableOrganizationInfo organizationInfo = ModifiableOrganizationInfo.create().setId(claims.getBody().getAudience());
		return organizationInfo.toImmutable();
	}
	
	public static OrganizationInfo of(com.nowellpoint.api.model.document.Organization source) {
		OrganizationInfo instance = OrganizationInfo.builder()
				.createdBy(UserInfo.of(source.getCreatedBy()))
				.domain(source.getDomain())
				.id(source.getId().toString())
				.lastUpdatedBy(UserInfo.of(source.getLastUpdatedBy()))
				.number(source.getNumber())
				.build();
		
		return instance;
				
	}
}
package com.nowellpoint.api.rest.domain;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.UserProfileResource;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = UserInfo.class)
@JsonDeserialize(as = UserInfo.class)
public abstract class AbstractUserInfo {
	public abstract String getId(); 
	public abstract @Nullable String getLastName();
	public abstract @Nullable String getFirstName();
	public abstract @Nullable String getName();
	public abstract @Nullable String getCompany();
	public abstract @Nullable String getEmail();
	public abstract @Nullable String getPhone();
	public abstract @Nullable String getMobilePhone();
	public abstract @Nullable Photos getPhotos();
	
	public Meta getMeta() {
		URI href = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(UserProfileResource.class)
				.path("/{id}")
				.build(Assert.isNotNullOrEmpty(getId()) ? getId() : "{id}");
				
		Meta meta = Meta.builder()
				.href(href.toString())
				.build();
		
		return meta;
	}
	
	public static UserInfo of(String id) {
		Assert.assertNotNullOrEmpty(id, "User Id cannot be null or empty");
		ModifiableUserInfo userInfo = ModifiableUserInfo.create().setId(id);
		return userInfo.toImmutable();
	}
	
	public static UserInfo of(Jws<Claims> claims) {
		Assert.assertNotNull(claims, "Jws claims cannot be null");
		ModifiableUserInfo userInfo = ModifiableUserInfo.create().setId(claims.getBody().getSubject());
		return userInfo.toImmutable();
	}
}
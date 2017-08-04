package com.nowellpoint.api.rest.domain;

import java.net.URI;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.SignUpService;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Registration.class)
@JsonDeserialize(as = Registration.class)
public abstract class AbstractRegistration extends AbstractImmutableResource {
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getCountryCode();
	public abstract @JsonIgnore String getEmailVerificationToken();
	public abstract @Nullable String getDomain();
	public abstract @Nullable URI getEmailVerificationHref();
	public abstract Long getExpiresAt();
	public abstract AbstractSubscription getSubscription();
	public abstract @Nullable String getIdentityHref(); 
	public abstract UserInfo getCreatedBy();
	public abstract UserInfo getLastUpdatedBy();
	
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	@Override
	public Meta getMeta() {
		return getMetaAs(SignUpService.class);
	}
	
	public static Registration of(com.nowellpoint.api.model.document.Registration source) {
		ModifiableRegistration registration = modelMapper.map(source, ModifiableRegistration.class);
		return registration.toImmutable();
	}
	
	@Override
	public void fromDocument(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Registration.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Registration.class);
	}
}
package com.nowellpoint.api.rest.domain;

import java.util.Date;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Registration.class)
@JsonDeserialize(as = Registration.class)
public abstract class AbstractRegistration extends AbstractResource {
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getCountryCode();
	public abstract String getEmailVerificationToken();
	public abstract Date getCreatedOn();
	public abstract Date getLastUpdatedOn();
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Registration.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Registration.class);
	}
}
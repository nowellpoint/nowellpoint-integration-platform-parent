package com.nowellpoint.console.model;

import java.util.Locale;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.util.MessageProvider;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Lead.class)
@JsonDeserialize(as = Lead.class)
public abstract class AbstractLead extends AbstractResource {
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getMessage();
	
	@Value.Default
	public Locale getLocale() {
		return Locale.getDefault();
	}
	
	@Value.Derived
	public String getSuccessMessage() {
		return MessageProvider.getMessage(getLocale(), "contact.confirmation.message");
	}
	
	public static Lead of(com.nowellpoint.console.entity.Lead entity) {
		return entity == null ? null : Lead.builder()
				.id(entity.getId().toString())
				.createdOn(entity.getCreatedOn())
				.email(entity.getEmail())
				.firstName(entity.getFirstName())
				.lastName(entity.getLastName())
				.lastUpdatedOn(entity.getLastUpdatedOn())
				.message(entity.getMessage())
				.build();
	}
}
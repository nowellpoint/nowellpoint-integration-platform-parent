package com.nowellpoint.console.model;

import java.util.Locale;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.www.app.util.MessageProvider;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Lead.class)
@JsonDeserialize(as = Lead.class)
public abstract class AbstractLead {
	public abstract @Nullable String getId();
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
				.email(entity.getEmail())
				.firstName(entity.getFirstName())
				.id(entity.getId().toString())
				.lastName(entity.getLastName())
				.message(entity.getMessage())
				.build();
	}
}
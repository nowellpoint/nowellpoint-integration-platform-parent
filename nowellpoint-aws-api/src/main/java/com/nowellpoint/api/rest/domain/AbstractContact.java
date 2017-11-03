package com.nowellpoint.api.rest.domain;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Contact.class)
@JsonDeserialize(as = Contact.class)
public abstract class AbstractContact {
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getAddedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	public static Contact of(com.braintreegateway.Customer source) {
		Contact instance = Contact.builder()
				.email(source.getEmail())
				.firstName(source.getFirstName())
				.lastName(source.getLastName())
				.phone(source.getPhone())
				.addedOn(source.getCreatedAt().getTime())
				.updatedOn(source.getUpdatedAt().getTime())
				.build();
		
		return instance;
	}
}
package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

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
	
	public String getName() {
		return Assert.isNotNullOrEmpty(getFirstName()) ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
}
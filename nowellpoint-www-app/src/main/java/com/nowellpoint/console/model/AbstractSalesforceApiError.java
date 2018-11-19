package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = SalesforceError.class)
@JsonDeserialize(as = SalesforceError.class)
public abstract class AbstractSalesforceApiError {
	public abstract String getError();
	public abstract String getErrorDescription();
}
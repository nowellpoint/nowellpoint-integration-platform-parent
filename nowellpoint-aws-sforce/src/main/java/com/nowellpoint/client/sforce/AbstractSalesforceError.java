package com.nowellpoint.client.sforce;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = SalesforceError.class)
@JsonDeserialize(as = SalesforceError.class)
public abstract class AbstractSalesforceError {
	public abstract @JsonProperty("error") String getError();
	public abstract @JsonProperty("errorDescription") String getErrorDescription();
	public abstract @JsonProperty("message") String getMessage();
	public abstract @JsonProperty("errorCode") String getErrorCode();
}
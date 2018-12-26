package com.nowellpoint.client.sforce;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = SalesforceApiError.class)
@JsonDeserialize(as = SalesforceApiError.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractSalesforceApiError {
	//public abstract @JsonProperty("error") String getError();
	//public abstract @JsonProperty("errorDescription") String getErrorDescription();
	public abstract @JsonProperty("message") String getMessage();
	public abstract @JsonProperty("errorCode") String getErrorCode();
}
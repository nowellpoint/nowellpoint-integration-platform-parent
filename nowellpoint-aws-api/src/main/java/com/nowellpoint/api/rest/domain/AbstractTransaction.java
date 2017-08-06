package com.nowellpoint.api.rest.domain;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractTransaction {
	public abstract String getId();
	public abstract @Nullable Double getAmount();
	public abstract @Nullable String getCurrencyIsoCode();
	public abstract String getStatus();
	public abstract @Nullable AbstractCreditCard getCreditCard();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getCreatedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
}
package com.nowellpoint.api.rest.domain;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = CreditCard.class)
@JsonDeserialize(as = CreditCard.class)
public abstract class AbstractCreditCard {
	public abstract String getCardType();
	public abstract String getCardholderName();
	public abstract @Nullable String getCvv();
	public abstract String getNumber();
	public abstract String getExpirationMonth();
	public abstract String getExpirationYear();
	public abstract @Nullable String getLastFour();
	public abstract @Nullable String getToken();
	public abstract @Nullable String getImageUrl();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getAddedOn();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
}
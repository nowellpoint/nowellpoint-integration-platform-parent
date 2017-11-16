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
	public abstract String getExpirationMonth();
	public abstract String getExpirationYear();
	public abstract @Nullable String getLastFour();
	public abstract @Nullable String getToken();
	public abstract @Nullable String getImageUrl();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getAddedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	
	public static CreditCard of(com.nowellpoint.api.model.document.CreditCard source) {
		CreditCard creditCard = ModifiableCreditCard.create()
				.setAddedOn(source.getAddedOn())
				.setCardholderName(source.getCardholderName())
				.setCardType(source.getCardType())
				.setExpirationMonth(source.getExpirationMonth())
				.setExpirationYear(source.getExpirationMonth())
				.setImageUrl(source.getImageUrl())
				.setLastFour(source.getLastFour())
				.setToken(source.getToken())
				.setUpdatedOn(source.getUpdatedOn())
				.toImmutable();
		
		return creditCard;
	}
	
	public static CreditCard of(com.braintreegateway.CreditCard source) {
		CreditCard instance = CreditCard.builder()
				.addedOn(source.getCreatedAt().getTime())
				.updatedOn(source.getUpdatedAt().getTime())
				.cardholderName(source.getCardholderName())
				.cardType(source.getCardType())
				.expirationMonth(source.getExpirationMonth())
				.expirationYear(source.getExpirationYear())
				.imageUrl(source.getImageUrl())
				.lastFour(source.getLast4())
				.token(source.getToken())
				.build();
		
		return instance;
	}
}
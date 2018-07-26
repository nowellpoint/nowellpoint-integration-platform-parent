package com.nowellpoint.console.model;

import java.time.Instant;
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
	public abstract String getExpirationMonth();
	public abstract String getExpirationYear();
	public abstract @Nullable String getLastFour();
	public abstract @Nullable String getToken();
	public abstract @Nullable String getImageUrl();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getAddedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	
	public static CreditCard of(com.nowellpoint.console.entity.CreditCard source) {
		return source == null ? null : CreditCard.builder()
				.addedOn(source.getAddedOn())
				.cardholderName(source.getCardholderName())
				.cardType(source.getCardType())
				.expirationMonth(source.getExpirationMonth())
				.expirationYear(source.getExpirationYear())
				.imageUrl(source.getImageUrl())
				.lastFour(source.getLastFour())
				.token(source.getToken())
				.updatedOn(source.getUpdatedOn())
				.build();
	}
	
	public static CreditCard of(com.braintreegateway.CreditCard source) {
		return source == null ? null : CreditCard.builder()
				.addedOn(source.getCreatedAt() != null ? source.getCreatedAt().getTime() : Date.from(Instant.now()))
				.addedOn(Date.from(Instant.now()))
				.cardholderName(source.getCardholderName())
				.cardType(source.getCardType())
				.expirationMonth(source.getExpirationMonth())
				.expirationYear(source.getExpirationYear())
				.imageUrl(source.getImageUrl())
				.lastFour(source.getLast4())
				.token(source.getToken())
				.updatedOn(Date.from(Instant.now()))
				.updatedOn(source.getUpdatedAt() != null ? source.getUpdatedAt().getTime() : Date.from(Instant.now()))
				.build();
	}
}
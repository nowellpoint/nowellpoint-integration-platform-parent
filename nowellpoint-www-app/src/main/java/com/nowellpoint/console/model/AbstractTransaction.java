package com.nowellpoint.console.model;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Transaction.class)
@JsonDeserialize(as = Transaction.class)
public abstract class AbstractTransaction {
	public abstract String getId();
	public abstract @Nullable Double getAmount();
	public abstract @Nullable String getCurrencyIsoCode();
	public abstract String getStatus();
	public abstract @Nullable CreditCard getCreditCard();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getCreatedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	
	public static Transaction of(com.nowellpoint.console.entity.Transaction source) {
		return source == null ? null : Transaction.builder()
				.amount(source.getAmount())
				.createdOn(source.getCreatedOn())
				.creditCard(CreditCard.of(source.getCreditCard()))
				.currencyIsoCode(source.getCurrencyIsoCode())
				.id(source.getId())
				.status(source.getStatus())
				.updatedOn(source.getUpdatedOn())
				.build();
	}
	
	public static Transaction of(com.braintreegateway.Transaction source) {
		return source == null ? null : Transaction.builder()
				.amount(source.getAmount().doubleValue())
				.createdOn(source.getCreatedAt().getTime())
				.creditCard(CreditCard.of(source.getCreditCard()))
				.currencyIsoCode(source.getCurrencyIsoCode())
				.id(source.getId())
				.status(source.getStatus().name())
				.updatedOn(source.getUpdatedAt().getTime())
				.build();
	}
}
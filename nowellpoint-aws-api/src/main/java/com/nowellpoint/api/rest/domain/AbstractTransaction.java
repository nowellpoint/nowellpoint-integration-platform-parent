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
	public abstract @Nullable CreditCard getCreditCard();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getCreatedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	
	public static Transaction of(com.braintreegateway.Transaction source) {
		Transaction instance = Transaction.builder()
				.amount(source.getAmount().doubleValue())
				.createdOn(source.getCreatedAt().getTime())
				.updatedOn(source.getUpdatedAt().getTime())
				.creditCard(CreditCard.of(source.getCreditCard()))
				.currencyIsoCode(source.getCurrencyIsoCode())
				.id(source.getId())
				.status(source.getStatus().name())
				.build();
		
		return instance;
	}
	
	public static Transaction of(com.nowellpoint.api.model.document.Transaction source) {
		Transaction instance = Transaction.builder()
				.amount(source.getAmount())
				.createdOn(source.getCreatedOn())
				.updatedOn(source.getUpdatedOn())
				.creditCard(CreditCard.of(source.getCreditCard()))
				.currencyIsoCode(source.getCurrencyIsoCode())
				.id(source.getId())
				.status(source.getStatus())
				.build();
		
		return instance;
	}
}
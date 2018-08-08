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
	public abstract String getPlan();
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract Address getBillingAddress();
	public abstract Date getBillingPeriodStartDate();
	public abstract Date getBillingPeriodEndDate();
	public abstract String getStatus();
	public abstract @Nullable CreditCard getCreditCard();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getCreatedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	
	@Value.Derived
	public String getName() {
		return getFirstName() != null ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	public static Transaction of(com.nowellpoint.console.entity.Transaction source) {
		return source == null ? null : Transaction.builder()
				.amount(source.getAmount())
				.billingAddress(Address.of(source.getBillingAddress()))
				.billingPeriodEndDate(source.getBillingPeriodEndDate())
				.billingPeriodStartDate(source.getBillingPeriodStartDate())
				.createdOn(source.getCreatedOn())
				.creditCard(CreditCard.of(source.getCreditCard()))
				.currencyIsoCode(source.getCurrencyIsoCode())
				.firstName(source.getFirstName())
				.lastName(source.getLastName())
				.id(source.getId())
				.plan(source.getPlan())
				.status(source.getStatus())
				.updatedOn(source.getUpdatedOn())
				.build();
	}
	
	public static Transaction of(com.braintreegateway.Transaction source) {
		return source == null ? null : Transaction.builder()
				.amount(source.getAmount().doubleValue())
				.billingAddress(Address.of(source.getBillingAddress()))
				.billingPeriodEndDate(source.getSubscriptionDetails().getBillingPeriodEndDate().getTime())
				.billingPeriodStartDate(source.getSubscriptionDetails().getBillingPeriodStartDate().getTime())
				.createdOn(source.getCreatedAt().getTime())
				.creditCard(CreditCard.of(source.getCreditCard()))
				.currencyIsoCode(source.getCurrencyIsoCode())
				.firstName(source.getCustomer().getFirstName())
				.lastName(source.getCustomer().getLastName())
				.id(source.getId())
				.plan(source.getPlanId())
				.status(source.getStatus().name())
				.updatedOn(source.getUpdatedAt().getTime())
				.build();
	}
}
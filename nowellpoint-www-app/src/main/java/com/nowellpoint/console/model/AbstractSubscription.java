package com.nowellpoint.console.model;

import java.util.Date;
import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"feature:features"})
@JsonSerialize(as = Subscription.class)
@JsonDeserialize(as = Subscription.class)
public abstract class AbstractSubscription {
	public abstract @Nullable String getNumber();
	public abstract String getPlanId();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getAddedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
	public abstract String getPlanName();
	public abstract String getPlanCode();
	public abstract String getCurrencyIsoCode();
	public abstract String getCurrencySymbol();
	public abstract Double getUnitPrice();
	public abstract String getBillingFrequency();
	public abstract @Nullable String getStatus();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getNextBillingDate();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd") Date getBillingPeriodStartDate();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd") Date getBillingPeriodEndDate();
	public abstract @Nullable CreditCard getCreditCard();
	public abstract @Nullable Address getBillingAddress();
	public abstract @Nullable Contact getBillingContact();
	public abstract Set<Feature> getFeatures();
	
	public static Subscription of(com.nowellpoint.console.entity.Subscription source) {
		return source == null ? null : Subscription.builder()
				.addedOn(source.getAddedOn())
				.billingAddress(Address.of(source.getBillingAddress()))
				.billingContact(Contact.of(source.getBillingContact()))
				.billingFrequency(source.getBillingFrequency())
				.creditCard(CreditCard.of(source.getCreditCard()))
				.currencyIsoCode(source.getCurrencyIsoCode())
				.currencySymbol(source.getCurrencySymbol())
				.features(AbstractFeatures.of(source.getFeatures()))
				.nextBillingDate(source.getNextBillingDate())
				.number(source.getNumber())
				.planCode(source.getPlanCode())
				.planId(source.getPlanId())
				.planName(source.getPlanName())
				.status(source.getStatus())
				.unitPrice(source.getUnitPrice())
				.updatedOn(source.getUpdatedOn())
				.build();
	}
}
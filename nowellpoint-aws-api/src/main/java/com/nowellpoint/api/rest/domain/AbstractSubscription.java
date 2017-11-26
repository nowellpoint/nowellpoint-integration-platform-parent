package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
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
	
	public static Subscription of(com.nowellpoint.api.model.document.Subscription source) {
		Set<Feature> features = new HashSet<>();
		for (com.nowellpoint.api.model.document.Feature feature : source.getFeatures()) {
			features.add(Feature.of(feature));
		}
		
		ModifiableSubscription subscription = ModifiableSubscription.create()
				.setAddedOn(source.getAddedOn())
				.setBillingAddress(Address.of(source.getBillingAddress()))
				.setBillingFrequency(source.getBillingFrequency())
				.setBillingPeriodEndDate(source.getBillingPeriodEndDate())
				.setBillingPeriodStartDate(source.getBillingPeriodStartDate())
				.setCreditCard(CreditCard.of(source.getCreditCard()))
				.setCurrencyIsoCode(source.getCurrencyIsoCode())
				.setCurrencySymbol(source.getCurrencySymbol())
				.setFeatures(features)
				.setNextBillingDate(source.getNextBillingDate())
				.setNumber(source.getNumber())
				.setPlanCode(source.getPlanCode())
				.setPlanId(source.getPlanId())
				.setPlanName(source.getPlanName())
				.setStatus(source.getStatus())
				.setUnitPrice(source.getUnitPrice())
				.setUpdatedOn(source.getUpdatedOn())
				.setBillingContact(Contact.of(source.getBillingContact()));
		
		return subscription.toImmutable();
	}
}
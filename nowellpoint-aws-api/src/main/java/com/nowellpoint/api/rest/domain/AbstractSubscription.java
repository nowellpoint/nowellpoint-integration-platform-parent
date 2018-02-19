package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	public static Subscription of(com.nowellpoint.api.model.document.Subscription source) {
		
		Address billingAddress = Address.of(source.getBillingAddress());
		
		Contact billingContact = Contact.of(source.getBillingContact());
		
		Set<Feature> featureList = source.getFeatures().stream()
			    .map(feature -> Feature.of(feature))
			    .collect(Collectors.toSet());
		
		ModifiableSubscription subscription = ModifiableSubscription.create()
				.setAddedOn(source.getAddedOn())
				.setBillingAddress(billingAddress)
				.setBillingFrequency(source.getBillingFrequency())
				.setBillingPeriodEndDate(source.getBillingPeriodEndDate())
				.setBillingPeriodStartDate(source.getBillingPeriodStartDate())
				.setCreditCard(CreditCard.of(source.getCreditCard()))
				.setCurrencyIsoCode(source.getCurrencyIsoCode())
				.setCurrencySymbol(source.getCurrencySymbol())
				.setFeatures(featureList)
				.setNextBillingDate(source.getNextBillingDate())
				.setNumber(source.getNumber())
				.setPlanCode(source.getPlanCode())
				.setPlanId(source.getPlanId())
				.setPlanName(source.getPlanName())
				.setStatus(source.getStatus())
				.setUnitPrice(source.getUnitPrice())
				.setUpdatedOn(source.getUpdatedOn())
				.setBillingContact(billingContact);
		
		return subscription.toImmutable();
	}
}
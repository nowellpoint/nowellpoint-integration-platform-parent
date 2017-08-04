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
@JsonSerialize(as = Subscription.class)
@JsonDeserialize(as = Subscription.class)
public abstract class AbstractSubscription {
	public abstract @Nullable String getSubscriptionId();
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
}
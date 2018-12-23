package com.nowellpoint.console.model;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = Plan.class)
@JsonDeserialize(as = Plan.class)
public abstract class AbstractPlan {
	public abstract String getId();
	public abstract Boolean getRecommendedPlan();
	public abstract String getPlanName();
	public abstract String getBillingFrequency();
	public abstract String getPlanCode();
	public abstract Boolean getIsActive();
	public abstract String getLanguage();
	public abstract Price getPrice();
	public abstract Set<StreamingEventListener> getEventListeners();
	public abstract Set<Feature> getFeatures();
	
	public static Plan of(com.nowellpoint.console.entity.Plan entity) {
		return Plan.builder()
				.id(entity.getId().toString())
				.billingFrequency(entity.getBillingFrequency())
				.features(Features.of(entity.getFeatures()))
				.isActive(entity.getIsActive())
				.language(entity.getLanguage())
				.planCode(entity.getPlanCode())
				.planName(entity.getPlanName())
				.price(Price.of(entity.getPrice()))
				.recommendedPlan(entity.getRecommendedPlan())
				.eventListeners(StreamingEventListeners.of(entity.getEventListeners()))
				.build();
	}
}
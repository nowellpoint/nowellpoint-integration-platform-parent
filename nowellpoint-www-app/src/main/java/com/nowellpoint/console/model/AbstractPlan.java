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
	public abstract Set<Feature> getFeatures();
	
	public static Plan of(com.nowellpoint.console.entity.Plan source) {
		return Plan.builder()
				.id(source.getId().toString())
				.billingFrequency(source.getBillingFrequency())
				.features(Features.of(source.getFeatures()))
				.isActive(source.getIsActive())
				.language(source.getLanguage())
				.planCode(source.getPlanCode())
				.planName(source.getPlanName())
				.price(Price.of(source.getPrice()))
				.recommendedPlan(source.getRecommendedPlan())
				.build();
	}
}
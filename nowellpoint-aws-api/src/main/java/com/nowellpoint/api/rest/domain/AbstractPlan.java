package com.nowellpoint.api.rest.domain;

import java.util.HashSet;
import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"feature:features"})
@JsonSerialize(as = Plan.class)
@JsonDeserialize(as = Plan.class)
public abstract class AbstractPlan  {
	public abstract Boolean getRecommendedPlan();
	public abstract String getLocale();
	public abstract String getLanguage();
	public abstract String getPlanName();
	public abstract String getPlanCode();
	public abstract String getBillingFrequency();
	public abstract Price getPrice();
	public abstract Set<Feature> getFeatures();
	
	public static Plan of(com.nowellpoint.api.model.document.Plan source) {
		
		Set<Feature> features = new HashSet<>();
		for (com.nowellpoint.api.model.document.Feature feature : source.getFeatures()) {
			features.add(Feature.of(feature));
		}
		
		Price price = Price.of(source.getPrice());
		
		ModifiablePlan target = ModifiablePlan.create()
				.setBillingFrequency(source.getBillingFrequency())
				.setFeatures(features)
				.setLanguage(source.getLanguage())
				.setLocale(source.getLocale())
				.setPlanCode(source.getPlanCode())
				.setPlanName(source.getPlanName())
				.setPrice(price)
				.setRecommendedPlan(source.getRecommendedPlan());
		
		return target.toImmutable();
	}
}
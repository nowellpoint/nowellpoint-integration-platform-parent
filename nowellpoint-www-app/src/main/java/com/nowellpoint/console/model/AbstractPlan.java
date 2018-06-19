package com.nowellpoint.console.model;

import java.util.ArrayList;
import java.util.List;

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
	public abstract List<Feature> getFeatures();
	
	public static Plan of(com.nowellpoint.console.entity.Plan source) {
		
		Price price = Price.builder()
				.currencyIsoCode(source.getPrice().getCurrencyIsoCode())
				.currencySymbol(source.getPrice().getCurrencySymbol())
				.unitPrice(source.getPrice().getUnitPrice())
				.build();
		
		List<Feature> features = new ArrayList<>();
		
		source.getFeatures().stream().forEach(f -> {
			features.add(Feature.of(f));
		});
		
		return Plan.builder()
				.id(source.getId().toString())
				.billingFrequency(source.getBillingFrequency())
				.features(features)
				.isActive(source.getIsActive())
				.language(source.getLanguage())
				.planCode(source.getPlanCode())
				.planName(source.getPlanName())
				.price(price)
				.recommendedPlan(source.getRecommendedPlan())
				.build();
	}
}
package com.nowellpoint.signup.model;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"feature:features"})
@JsonSerialize(as = Plan.class)
@JsonDeserialize(as = Plan.class)
public abstract class AbstractPlan {
	public abstract Boolean getRecommendedPlan();
	public abstract String getLocale();
	public abstract String getLanguage();
	public abstract String getPlanName();
	public abstract String getPlanCode();
	public abstract String getBillingFrequency();
	//public abstract Price getPrice();
	//public abstract Set<Feature> getFeatures();

}
package com.nowellpoint.content.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan {
	
	private String id;
	private Boolean recommendedPlan;
	private String planName;
	private String billingFrequency;
	private String planCode;
	private Boolean isActive;
	private Price price;
	private List<Feature> features;
	
	public Plan() {
		
	}

	public String getId() {
		return id;
	}

	public Boolean getRecommendedPlan() {
		return recommendedPlan;
	}

	public String getPlanName() {
		return planName;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public String getPlanCode() {
		return planCode;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public Price getPrice() {
		return price;
	}

	public List<Feature> getFeatures() {
		return features;
	}
}
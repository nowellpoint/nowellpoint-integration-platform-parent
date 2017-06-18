package com.nowellpoint.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan extends AbstractResource {
	
	private Boolean recommendedPlan;
	
	private String locale;
	
	private String language;

	private String planName;
	
	private String planCode;
	
	private String billingFrequency;
	
	private Price price;
	
	private List<Feature> features;
	
	public Plan() {
		
	}

	public Boolean getRecommendedPlan() {
		return recommendedPlan;
	}

	public void setRecommendedPlan(Boolean recommendedPlan) {
		this.recommendedPlan = recommendedPlan;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}	
}
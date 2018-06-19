package com.nowellpoint.console.entity;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "plans")
public class Plan extends BaseEntity {

	private static final long serialVersionUID = 1006163003393384824L;
	
	private Boolean recommendedPlan;
	
	private String planName;
	
	private String billingFrequency;
	
	private String planCode;
	
	private Boolean isActive;
	
	private String language;
	
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

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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
package com.nowellpoint.api.rest.domain;

import java.util.Set;

import com.nowellpoint.mongodb.document.MongoDocument;

public class PlanOrig extends AbstractResource {
	
	private Boolean recommendedPlan;

	private String locale;
	
	private String language;
	
	private String planName;
	
	private String planCode;
	
	private String billingFrequency;
	
	private PriceOrig priceOrig;
	
	private Set<FeatureOrig> featureOrigs;
	
	public PlanOrig() {
		
	}
	
	private <T> PlanOrig(T document) {
		modelMapper.map(document, this);
	}
	
	public static PlanOrig of(MongoDocument document) {
		return new PlanOrig(document);
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

	public Set<FeatureOrig> getFeatures() {
		return featureOrigs;
	}

	public void setFeatures(Set<FeatureOrig> featureOrigs) {
		this.featureOrigs = featureOrigs;
	}

	public PriceOrig getPrice() {
		return priceOrig;
	}

	public void setPrice(PriceOrig priceOrig) {
		this.priceOrig = priceOrig;
	}

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Plan.class);
	}	
}
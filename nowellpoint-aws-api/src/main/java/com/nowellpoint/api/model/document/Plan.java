package com.nowellpoint.api.model.document;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.api.model.codec.PlanCodec;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="plans", codec=PlanCodec.class)
public class Plan extends MongoDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -7569793449815113870L;
	
	private Meta meta;
	
	private UserRef createdBy;
	
	private UserRef lastModifiedBy;
	
	private Boolean recommendedPlan;
	
	private String localeSidKey;
	
	private String languageLocaleKey;
	
	private String planName;
	
	private String planCode;
	
	private String billingFrequency;
	
	private Boolean isActive;
	
	private Price price;
	
	private Set<Service> services;
	
	public Plan() {
		
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Boolean getRecommendedPlan() {
		return recommendedPlan;
	}

	public void setRecommendedPlan(Boolean recommendedPlan) {
		this.recommendedPlan = recommendedPlan;
	}

	public UserRef getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserRef createdBy) {
		this.createdBy = createdBy;
	}

	public UserRef getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserRef lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getLocaleSidKey() {
		return localeSidKey;
	}

	public void setLocaleSidKey(String localeSidKey) {
		this.localeSidKey = localeSidKey;
	}

	public String getLanguageLocaleKey() {
		return languageLocaleKey;
	}

	public void setLanguageLocaleKey(String languageLocaleKey) {
		this.languageLocaleKey = languageLocaleKey;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public Set<Service> getServices() {
		return services;
	}

	public void setServices(Set<Service> services) {
		this.services = services;
	}	
}
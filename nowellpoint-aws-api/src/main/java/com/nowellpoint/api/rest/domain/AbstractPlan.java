package com.nowellpoint.api.rest.domain;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.PlanResource;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, depluralize = true, depluralizeDictionary = {"feature:features"})
@JsonSerialize(as = Plan.class)
@JsonDeserialize(as = Plan.class)
public abstract class AbstractPlan extends AbstractImmutableResource {
	public abstract @JsonIgnore UserInfo getCreatedBy();
	public abstract @JsonIgnore UserInfo getLastUpdatedBy();
	public abstract Boolean getRecommendedPlan();
	public abstract String getLocale();
	public abstract String getLanguage();
	public abstract String getPlanName();
	public abstract String getPlanCode();
	public abstract String getBillingFrequency();
	public abstract Price getPrice();
	public abstract Set<Feature> getFeatures();
	
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(PlanResource.class)
				.build();
	}
	
	@Override
	public void replace(MongoDocument document) {
		modelMapper.map(document, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Plan.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Plan.class);
	}
	
	public static Plan of(com.nowellpoint.api.model.document.Plan source) {
		
		Price price = Price.of(source.getPrice());
		
		Set<Feature> featureList = source.getFeatures().stream()
			    .map(feature -> Feature.of(feature))
			    .collect(Collectors.toSet());
		
		ModifiablePlan target = ModifiablePlan.create()
				.setId(source.getId().toString())
				.setCreatedBy(modelMapper.map(source.getCreatedBy(), ModifiableUserInfo.class).toImmutable())
				.setLastUpdatedBy(modelMapper.map(source.getLastUpdatedBy(), ModifiableUserInfo.class).toImmutable())
				.setCreatedOn(source.getCreatedOn())
				.setLastUpdatedOn(source.getLastUpdatedOn())
				.setBillingFrequency(source.getBillingFrequency())
				.setFeatures(featureList)
				.setLanguage(source.getLanguage())
				.setLocale(source.getLocale())
				.setPlanCode(source.getPlanCode())
				.setPlanName(source.getPlanName())
				.setPrice(price)
				.setRecommendedPlan(source.getRecommendedPlan());
		
		return target.toImmutable();
	}
}
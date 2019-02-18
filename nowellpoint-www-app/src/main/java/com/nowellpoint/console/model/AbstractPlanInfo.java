package com.nowellpoint.console.model;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true)
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = Plan.class)
@JsonDeserialize(as = Plan.class)
public abstract class AbstractPlanInfo {
	public abstract String getId();
	public abstract String getPlanName();
	public abstract String getBillingFrequency();
	public abstract String getPlanCode();
	public abstract Price getPrice();
	public abstract Set<Feature> getFeatures();

}
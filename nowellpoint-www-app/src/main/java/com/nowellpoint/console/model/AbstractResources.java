package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Resources.class)
@JsonDeserialize(as = Resources.class)
public abstract class AbstractResources {
	public abstract String getUserProfile();
	public abstract String getOrganization();
	public abstract String getConnectors();
	public abstract String getJobs();
}
package com.nowellpoint.console.model;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = Dashboard.class)
@JsonDeserialize(as = Dashboard.class)
public abstract class AbstractDashboard {
	
	public abstract List<UserLicense> getUserLicenses();
	public abstract Limits getLimits();
	
	@Value.Default
	public MetadataComponent getCustomObject() {
		return MetadataComponent.builder().build();
	}
	
	@Value.Default
	public MetadataComponent getApexClass() {
		return MetadataComponent.builder().build();
	}
	
	@Value.Default
	public MetadataComponent getApexTrigger() {
		return MetadataComponent.builder().build();
	}
	
	@Value.Default
	public MetadataComponent getRecordType() {
		return MetadataComponent.builder().build();
	}
	
	@Value.Default
	public MetadataComponent getUserRole() {
		return MetadataComponent.builder().build();
	}
	
	@Value.Default
	public MetadataComponent getProfile() {
		return MetadataComponent.builder().build();
	}
	
	@Value.Default
	public Date getLastRefreshedOn() {
		return Date.from(Instant.now());
	}
	
	public static Dashboard of(com.nowellpoint.console.entity.Dashboard source) {
		return source == null ? null : Dashboard.builder()
				.apexClass(MetadataComponent.of(source.getApexClass()))
				.apexTrigger(MetadataComponent.of(source.getApexTrigger()))
				.customObject(MetadataComponent.of(source.getCustomObject()))
				.lastRefreshedOn(source.getLastRefreshedOn())
				.limits(Limits.of(source.getLimits()))
				.profile(MetadataComponent.of(source.getProfile()))
				.userLicenses(UserLicenses.of(source.getUserLicenses()))
				.userRole(MetadataComponent.of(source.getUserRole()))
				.recordType(MetadataComponent.of(source.getRecordType()))
				.build();
	}
}
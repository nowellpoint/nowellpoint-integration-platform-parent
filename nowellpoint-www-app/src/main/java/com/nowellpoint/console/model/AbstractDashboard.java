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
	public DashboardComponent getCustomObject() {
		return DashboardComponent.builder().build();
	}
	
	@Value.Default
	public DashboardComponent getApexClass() {
		return DashboardComponent.builder().build();
	}
	
	@Value.Default
	public DashboardComponent getApexTrigger() {
		return DashboardComponent.builder().build();
	}
	
	@Value.Default
	public DashboardComponent getRecordType() {
		return DashboardComponent.builder().build();
	}
	
	@Value.Default
	public DashboardComponent getUserRole() {
		return DashboardComponent.builder().build();
	}
	
	@Value.Default
	public DashboardComponent getProfile() {
		return DashboardComponent.builder().build();
	}
	
	@Value.Default
	public Date getLastRefreshedOn() {
		return Date.from(Instant.now());
	}
	
	@Value.Default
	public Integer getCustomObjectCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getApexClassCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getApexTriggerCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getRecordTypeCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getUserRoleCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getProfileCount() {
		return 0;
	}
	
	public static Dashboard of(com.nowellpoint.console.entity.Dashboard source) {
		return source == null ? null : Dashboard.builder()
				.apexClass(DashboardComponent.of(source.getApexClass()))
				.apexTrigger(DashboardComponent.of(source.getApexTrigger()))
				.customObject(DashboardComponent.of(source.getCustomObject()))
				.lastRefreshedOn(source.getLastRefreshedOn())
				.limits(Limits.of(source.getLimits()))
				.profile(DashboardComponent.of(source.getProfile()))
				.userLicenses(UserLicenses.of(source.getUserLicenses()))
				.userRole(DashboardComponent.of(source.getUserRole()))
				.recordType(DashboardComponent.of(source.getRecordType()))
				.build();
	}
}
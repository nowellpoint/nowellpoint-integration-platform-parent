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
	
	public static Dashboard of(com.nowellpoint.console.entity.Dashboard entity) {
		return entity == null ? null : Dashboard.builder()
				.apexClassCount(entity.getApexClassCount())
				.apexTriggerCount(entity.getApexTriggerCount())
				.customObjectCount(entity.getCustomObjectCount())
				.lastRefreshedOn(entity.getLastRefreshedOn())
				.profileCount(entity.getProfileCount())
				.userLicenses(UserLicenses.of(entity.getUserLicenses()))
				.userRoleCount(entity.getUserRoleCount())
				.recordTypeCount(entity.getRecordTypeCount())
				.build();
	}
}
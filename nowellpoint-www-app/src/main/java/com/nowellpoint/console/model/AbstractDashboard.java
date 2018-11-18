package com.nowellpoint.console.model;

import java.time.Instant;
import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = Dashboard.class)
@JsonDeserialize(as = Dashboard.class)
public abstract class AbstractDashboard {
	
	@Value.Default
	public Date getLastRefreshedOn() {
		return Date.from(Instant.now());
	}
	
	@Value.Default
	public Integer getCustomObjectCount() {
		return 0;
	}
	
	public static Dashboard of(com.nowellpoint.console.entity.Dashboard entity) {
		return entity == null ? null : Dashboard.builder()
				.customObjectCount(entity.getCustomObjectCount())
				.lastRefreshedOn(entity.getLastRefreshedOn())
				.build();
	}
}
package com.nowellpoint.console.model;

import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = Dashboard.class)
@JsonDeserialize(as = Dashboard.class)
public abstract class AbstractDashboard extends AbstractResource {
	public abstract Date getLastRefreshedOn();
	
	public static Dashboard of(com.nowellpoint.console.entity.Dashboard entity) {
		return entity == null ? null : Dashboard.builder()
				.id(entity.getId().toString())
				.createdBy(UserInfo.of(entity.getCreatedBy()))
				.createdOn(entity.getCreatedOn())
				.lastRefreshedOn(entity.getLastRefreshedOn())
				.lastUpdatedBy(UserInfo.of(entity.getLastUpdatedBy()))
				.lastUpdatedOn(entity.getLastUpdatedOn())
				.build();
	}
}
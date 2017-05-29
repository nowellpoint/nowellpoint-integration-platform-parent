package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public abstract class AbstractRunWhenSubmitted implements AbstractSchedule {
	
	@Override
	public String getScheduleOption() {
		return "RUN_WHEN_SUBMITTED";
	}
}
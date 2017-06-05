package com.nowellpoint.api.rest.domain;

import java.util.Date;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public abstract class AbstractRunOnce implements AbstractSchedule {
	public abstract Date getRunDate();
	
	@Override
	public String getScheduleOption() {
		return "RUN_ONCE";
	}
}
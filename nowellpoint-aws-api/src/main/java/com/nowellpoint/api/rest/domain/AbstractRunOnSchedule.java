package com.nowellpoint.api.rest.domain;

import java.util.Date;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public abstract class AbstractRunOnSchedule implements AbstractSchedule {
	public abstract Date getStartAt();
	public abstract Date getEndAt();
	public abstract String getTimeZone();
	public abstract String getTimeUnit();
	public abstract Integer getTimeInterval();
	
	@Override
	public String getScheduleOption() {
		return "RUN_ON_SCHEDULE";
	}
}
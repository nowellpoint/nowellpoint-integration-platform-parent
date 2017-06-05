package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public abstract class AbstractRunOnSchedule implements AbstractSchedule {
	public abstract Date getStartAt();
	public abstract Optional<Date> getEndAt();
	public abstract TimeZone getTimeZone();
	public abstract TimeUnit getTimeUnit();
	public abstract Integer getTimeInterval();
	
	@Override
	public String getScheduleOption() {
		return "RUN_ON_SCHEDULE";
	}
}
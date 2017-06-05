package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public class AbstractRunOnSpecificDays implements AbstractSchedule {

	@Override
	public String getScheduleOption() {
		return "RUN_ON_SPECIFIC_DAYS";
	}
}
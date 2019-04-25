package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = EventStreamMonitor.class)
@JsonDeserialize(as = EventStreamMonitor.class)
public class AbstractEventStreamMonitor {

	@Value.Default
	public Long getCountToday() {
		return Long.valueOf(0);
	}
	
	@Value.Default
	public Long getCountThisWeek() {
		return Long.valueOf(0);
	}
	
	@Value.Default
	public Long getCountThisMonth() {
		return Long.valueOf(0);
	}
	
	@Value.Default
	public Long getCountThisYear() {
		return Long.valueOf(0);
	}
}
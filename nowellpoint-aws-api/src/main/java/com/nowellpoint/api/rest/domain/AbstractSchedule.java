package com.nowellpoint.api.rest.domain;

public interface AbstractSchedule {
	public String getScheduleOption();
	
	@SuppressWarnings("unchecked")
	public default <T extends AbstractSchedule> T getSchedule(Class<T> type) {
		return (T) this;
	}
}
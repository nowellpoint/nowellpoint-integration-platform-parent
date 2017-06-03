package com.nowellpoint.client.test;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class TestScheduleBuilder {
	
	@Test
	public void testCalendarDateBuiler() {
		
		Calendar start = Calendar.getInstance(TimeZone.getDefault());
		start.set(Calendar.HOUR, 3);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.AM_PM, Calendar.AM);
		
		System.out.println(start.getTime());
		
		if (Calendar.getInstance(TimeZone.getDefault()).after(start)) {
			start.roll(Calendar.DATE, 1);
		}
		
		final Calendar calendar = new Calendar.Builder()
				.setInstant(start.toInstant().toEpochMilli())
			    .setTimeZone(TimeZone.getDefault())
			    .build();
		
		System.out.println(calendar.getTime());
	}
}
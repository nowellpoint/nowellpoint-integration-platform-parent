package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nowellpoint.util.Assert;

public class Schedule {
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date runAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date start;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date end;
	
	private String timeZone;
	
	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year;
	
	public Schedule() {
		
	}
	
	private Schedule(
			Date runAt,
			Date start, 
			Date end, 
			String timeZone, 
			String seconds, 
			String minutes, 
			String hours, 
			String dayOfMonth, 
			String month, 
			String dayOfWeek, 
			String year) {
		
		this.runAt = runAt;
		this.dayOfMonth = dayOfMonth;
		this.dayOfWeek = dayOfWeek;
		this.hours = hours;
		this.end = end;
		this.minutes = minutes;
		this.month = month;
		this.seconds = seconds;
		this.start = start;
		this.timeZone = timeZone;
		this.year = year;
	}
	
	public static Schedule of(Date runAt, Date start, Date end, String timeZone, Calendar calendar) {
		String seconds = String.valueOf(calendar.get(Calendar.SECOND));
		String minutes = String.valueOf(calendar.get(Calendar.MINUTE));
		String hours = String.valueOf(calendar.get(Calendar.HOUR));
		String month = String.valueOf(calendar.get(Calendar.MONTH));
		String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String dayOfWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
		
		if (Assert.isEmpty(seconds)) {
			seconds = null;
		}
		
		if (Assert.isEmpty(minutes)) {
			minutes = null;
		}
		
		if (Assert.isEmpty(hours)) {
			hours = null;
		}
		
		if (Assert.isEmpty(month)) {
			month = null;
		}
		
		if (Assert.isEmpty(dayOfMonth)) {
			dayOfMonth = null;
		}
		
		if (Assert.isEmpty(year)) {
			year = null;
		}
		
		if (Assert.isEmpty(dayOfWeek)) {
			dayOfWeek = null;
		}
		
		return new Schedule(
				runAt,
				start, 
				end, 
				timeZone, 
				seconds, 
				minutes, 
				hours, 
				dayOfMonth, 
				month, 
				dayOfWeek, 
				year);
		
	}
	
	public static Schedule of(
			Date runAt,
			Date start, 
			Date end, 
			String timeZone, 
			String seconds, 
			String minutes, 
			String hours, 
			String dayOfMonth, 
			String month, 
			String dayOfWeek, 
			String year) {
		
		return new Schedule(
				runAt,
				start, 
				end, 
				timeZone, 
				seconds, 
				minutes, 
				hours, 
				dayOfMonth, 
				month, 
				dayOfWeek, 
				year);
	}
	
	public static Schedule runWhenSubmitted() {
		return new Schedule(
				Date.from(Instant.now()),
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null);
	}
	
	public static Schedule runOnce(Date runAt) {
		return new Schedule(
				runAt,
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null);
		
	}
	
	public static Schedule runOnSchedule() {
		return new Schedule(
				null,
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null);
	}
	
	public static Schedule runOnSpecficDays() {
		return new Schedule(
				null,
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null);
	}

	public Date getRunAt() {
		return runAt;
	}

	public void setRunAt(Date runAt) {
		this.runAt = runAt;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getSeconds() {
		return seconds;
	}

	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Schedule {
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date runAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date startAt;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date endAt;
	
	private TimeZone timeZone;
	
	private TimeUnit timeUnit;
	
	private Integer timeInterval;
	
	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year;
	
	private Schedule() {
		
	}
	
	private Schedule(
			Date runAt,
			Date startAt, 
			Date endAt, 
			TimeZone timeZone, 
			TimeUnit timeUnit,
			Integer timeInterval,
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
		this.endAt = endAt;
		this.timeUnit = timeUnit;
		this.timeInterval = timeInterval;
		this.minutes = minutes;
		this.month = month;
		this.seconds = seconds;
		this.startAt = startAt;
		this.timeZone = timeZone;
		this.year = year;
	}
	
	public static Schedule of(AbstractSchedule schedule) {
		if (schedule instanceof RunWhenSubmitted) {
			return of(schedule.getSchedule(RunWhenSubmitted.class));
		} else if (schedule instanceof RunOnce) {
			return of(schedule.getSchedule(RunOnce.class));
		} else if (schedule instanceof RunOnSchedule) {
			return of(schedule.getSchedule(RunOnSchedule.class));
		} else if (schedule instanceof RunOnSpecificDays) {
			return of(schedule.getSchedule(RunOnSpecificDays.class));
		} else {
			return null;
		}
	}
	
	public static Schedule of(RunWhenSubmitted runWhenSubmitted) {
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
				null, 
				null, 
				null);
	}
	
	public static Schedule of(RunOnce runOnce) {
		return new Schedule(
				runOnce.getRunDate(),
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
				null, 
				null);
	}
	
	public static Schedule of(RunOnSchedule runOnSchedule) {
		return new Schedule(
				null,
				runOnSchedule.getStartAt(), 
				runOnSchedule.getEndAt().orElse(null), 
				runOnSchedule.getTimeZone(), 
				runOnSchedule.getTimeUnit(),
				runOnSchedule.getTimeInterval(),
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null);
	}
	
	public static Schedule of(RunOnSpecificDays runOnSpecificDays) {
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

	public Date getStartAt() {
		return startAt;
	}

	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}

	public Date getEndAt() {
		return endAt;
	}

	public void setEndAt(Date endAt) {
		this.endAt = endAt;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public Integer getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(Integer timeInterval) {
		this.timeInterval = timeInterval;
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
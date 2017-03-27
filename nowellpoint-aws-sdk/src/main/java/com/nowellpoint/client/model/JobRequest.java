package com.nowellpoint.client.model;

import java.util.Date;

public class JobRequest {
	
	public static final String RUN_WHEN_SUBMITTED = "RUN_WHEN_SUBMITTED";
	public static final String ONCE = "ONCE";
	public static final String SCHEDULE = "SCHEDULE";
	public static final String SPECIFIC_DAYS = "SPECIFIC_DAYS";
	
	private String notificationEmail;
	
	private String scheduleOption;
	
	private String description;
	
	private Date start;
	
	private Date end;
	
	private String timeZone;
	
	private String jobName;
	
	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year; 
	
	public JobRequest() {
		
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public String getScheduleOption() {
		return scheduleOption;
	}

	public void setScheduleOption(String scheduleOption) {
		this.scheduleOption = scheduleOption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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
	
	public JobRequest withNotificationEmail(String notificationEmail) {
		setNotificationEmail(notificationEmail);
		return this;
	}
	
	public JobRequest withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public JobRequest withScheduleOption(String scheduleOption) {
		setScheduleOption(scheduleOption);
		return this;
	}
	
	public JobRequest withStart(Date start) {
		setStart(start);
		return this;
	}
	
	public JobRequest withEnd(Date end) {
		setEnd(end);
		return this;
	}
	
	public JobRequest withTimeZone(String timeZone) {
		setTimeZone(timeZone);
		return this;
	}
	
	public JobRequest withJobName(String jobName) {
		setJobName(jobName);
		return this;
	}
	
	public JobRequest withSeconds(String seconds) {
		setSeconds(seconds);
		return this;
	}
	
	public JobRequest withMinutes(String minutes) {
		setMinutes(minutes);
		return this;
	}
	
	public JobRequest withHours(String hours) {
		setHours(hours);
		return this;
	}
	
	public JobRequest withDayOfMonth(String dayOfMonth) {
		setDayOfMonth(dayOfMonth);
		return this;
	}
	
	public JobRequest withMonth(String month) {
		setMonth(month);
		return this;
	}
	
	public JobRequest withDayOfWeek(String dayOfWeek) {
		setDayOfWeek(dayOfWeek);
		return this;
	}
	
	public JobRequest withYear(String year) {
		setYear(year);
		return this;
	}
}
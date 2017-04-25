package com.nowellpoint.client.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.nowellpoint.util.Assert;

public class JobRequest {
	
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final String RUN_WHEN_SUBMITTED = "RUN_WHEN_SUBMITTED";
	public static final String ONCE = "ONCE";
	public static final String SCHEDULE = "SCHEDULE";
	public static final String SPECIFIC_DAYS = "SPECIFIC_DAYS";
	
	private String notificationEmail;
	
	private String scheduleOption;
	
	private String description;
	
	private Date runAt;
	
	private Date startAt;
	
	private Date endAt;
	
	private String timeZone;
	
	private String timeUnit;
	
	private Integer timeInterval;
	
	private String jobTypeId;
	
	private String connectorId;
	
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

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public Integer getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(Integer timeInterval) {
		this.timeInterval = timeInterval;
	}

	public String getJobTypeId() {
		return jobTypeId;
	}

	public void setJobTypeId(String jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
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
	
	public JobRequest withRunAt(Date runAt) {
		setRunAt(runAt);
		return this;
	}
	
	public JobRequest withRunAt(String runAt) throws ParseException {
		if (Assert.isNotNullOrEmpty(runAt)) {
			setRunAt(dateTimeFormat.parse(runAt));
		}
		return this;
	}
	
	public JobRequest withScheduleOption(String scheduleOption) {
		setScheduleOption(scheduleOption);
		return this;
	}
	
	public JobRequest withStartAt(Date startAt) {
		setStartAt(startAt);
		return this;
	}
	
	public JobRequest withStartAt(String startAt) throws ParseException {
		if (Assert.isNotNullOrEmpty(startAt)) {
			setStartAt(dateTimeFormat.parse(startAt));
		}
		return this;
	}
	
	public JobRequest withEndAt(Date end) {
		setEndAt(endAt);
		return this;
	}
	
	public JobRequest withEndAt(String endAt) throws ParseException {
		if (Assert.isNotNullOrEmpty(endAt)) {
			setEndAt(dateTimeFormat.parse(endAt));
		}
		return this;
	}
	
	public JobRequest withTimeZone(String timeZone) {
		setTimeZone(timeZone);
		return this;
	}
	
	public JobRequest withTimeUnit(String timeUnit) {
		setTimeUnit(timeUnit);
		return this;
	}
	
	public JobRequest withTimeInterval(Integer timeInterval) {
		setTimeInterval(timeInterval);
		return this;
	}
	
	public JobRequest withTimeInterval(String timeInterval) {
		setTimeInterval(Integer.valueOf(timeInterval));
		return this;
	}
	
	public JobRequest withJobTypeId(String jobTypeId) {
		setJobTypeId(jobTypeId);
		return this;
	}
	
	public JobRequest withConnectorId(String connectorId) {
		setConnectorId(connectorId);
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
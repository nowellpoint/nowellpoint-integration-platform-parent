package com.nowellpoint.api.rest.domain;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", deepImmutablesDetection = true)
public abstract class AbstractCreateJobRequest {
	public abstract Optional<String> getNotificationEmail();
	public abstract Optional<String> getSlackWebhookUrl();
	public abstract Optional<String> getRunAt();
	public abstract Optional<String> getDayOfMonth();
	public abstract Optional<String> getDayOfWeek();
	public abstract Optional<String> getDescription();
	public abstract Optional<String> getHours();
	public abstract Optional<String> getEndAt();
	public abstract Optional<String> getMinutes();
	public abstract Optional<String> getMonth();
	public abstract Optional<String> getSeconds();
	public abstract Optional<String> getStartAt();
	public abstract Optional<String> getTimeZone();
	public abstract Optional<String> getTimeUnit();
	public abstract Optional<String> getTimeInterval();
	public abstract Optional<String> getYear();
	public abstract Optional<String> getScheduleOption();
	public abstract JobType getJobType();
	public abstract Source getSource();
	public abstract Optional<AbstractSchedule> getSchedule();
}
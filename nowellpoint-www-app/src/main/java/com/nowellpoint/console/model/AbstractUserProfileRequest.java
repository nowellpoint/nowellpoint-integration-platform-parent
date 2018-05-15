package com.nowellpoint.console.model;

import java.time.Instant;
import java.util.Date;

import javax.annotation.Nullable;

import org.bson.types.ObjectId;
import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractUserProfileRequest {
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract @Nullable String getPhone();
	public abstract @Nullable String getTitle();
	public abstract String getLocale();
	public abstract String getTimeZone();
	
	private final Date now = Date.from(Instant.now());
	
	@Value.Default
	public ObjectId getId() {
		return new ObjectId();
	}
	
	@Value.Default
	public Date getCreatedOn() {
		return now;
	}
	
	@Value.Default
	public Date getLastUpdatedOn() {
		return now;
	}
}
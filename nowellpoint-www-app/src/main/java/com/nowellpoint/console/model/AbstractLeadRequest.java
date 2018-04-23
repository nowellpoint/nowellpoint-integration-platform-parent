package com.nowellpoint.console.model;

import java.util.Date;
import java.time.Instant;

import org.bson.types.ObjectId;
import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractLeadRequest {
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getMessage();
	
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
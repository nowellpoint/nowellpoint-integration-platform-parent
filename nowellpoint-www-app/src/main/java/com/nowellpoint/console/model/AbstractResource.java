package com.nowellpoint.console.model;

import java.time.Instant;
import java.util.Date;

import javax.annotation.Nullable;

import org.bson.types.ObjectId;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "createdOn", "lastUpdatedOn" })
public abstract class AbstractResource {
	public abstract @Nullable UserInfo getCreatedBy();
	public abstract @Nullable UserInfo getLastUpdatedBy();
	public abstract @Nullable Meta getMeta();
	
	private Date now = Date.from(Instant.now());
	
	@Value.Default
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public Date getCreatedOn() {
		return now;
	}
	
	@Value.Default
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public Date getLastUpdatedOn() {
		return now;
	}
	
	@Value.Default
	public String getId() {
		return new ObjectId().toString();
	}
}
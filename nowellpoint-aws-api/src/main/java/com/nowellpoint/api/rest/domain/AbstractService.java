package com.nowellpoint.api.rest.domain;

import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Service.class)
@JsonDeserialize(as = Service.class)
public abstract class AbstractService {
	public abstract String getServiceId();
	public abstract String getName();
	public abstract String getType();
	public abstract String getTemplate();
	public abstract String getHref();
	public abstract String getWhatId();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getAddedOn();
	public abstract @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getUpdatedOn();
}
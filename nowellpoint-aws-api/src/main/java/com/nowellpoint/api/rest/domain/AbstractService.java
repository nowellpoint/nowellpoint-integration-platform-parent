package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Service.class)
@JsonDeserialize(as = Service.class)
public abstract class AbstractService {
	public abstract String getId();
	public abstract String getName();
	public abstract String getType();
	public abstract Boolean getIsEnabled();
	
	public static Service of(com.nowellpoint.api.model.document.Service source) {
		Service service = Service.builder()
				.id(source.getId())
				.isEnabled(source.getIsEnabled())
				.name(source.getName())
				.type(source.getType())
				.build();
		
		return service;
	}
}
package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = ReferenceLink.class)
@JsonDeserialize(as = ReferenceLink.class)
public abstract class AbstractReferenceLink {
	public abstract String getId();
	public abstract String getType();
	
	public static ReferenceLink of(ReferenceLinkTypes type, String id) {
		return ReferenceLink.builder()
				.id(id)
				.type(type.name())
				.build();
	}
}
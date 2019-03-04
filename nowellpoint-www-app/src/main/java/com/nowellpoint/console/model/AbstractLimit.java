package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = Limit.class)
@JsonDeserialize(as = Limit.class)
public abstract class AbstractLimit {
	@Value.Default
	public Long getMax() {
		return Long.valueOf(0);
	}
	
	@Value.Default
	public Long getRemaining() {
		return Long.valueOf(0);
	}
	
	public static Limit of (com.nowellpoint.client.sforce.model.Limit source) {
		return source == null ? Limit.builder().build() : Limit.builder()
				.max(source.getMax())
				.remaining(source.getRemaining())
				.build();
	}
	
	public static Limit of(com.nowellpoint.console.entity.Limit source) {
		return source == null ? Limit.builder().build() : Limit.builder()
				.max(source.getMax())
				.remaining(source.getRemaining())
				.build();
	}
}
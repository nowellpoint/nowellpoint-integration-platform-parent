package com.nowellpoint.console.model;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractEventStreamListeners {
	public static Set<EventStreamListener> of(Set<com.nowellpoint.console.entity.EventStreamListener> source) {
		return source == null ? Collections.emptySet() : source.stream()
				.map(f -> EventStreamListener.of(f))
				.collect(Collectors.toSet());
	}
}
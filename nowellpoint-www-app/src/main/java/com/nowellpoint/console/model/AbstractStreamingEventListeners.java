package com.nowellpoint.console.model;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractStreamingEventListeners {
	public static Set<StreamingEventListener> of(Set<com.nowellpoint.console.entity.StreamingEventListener> source) {
		return source == null ? Collections.emptySet() : source.stream()
				.map(f -> {
					return StreamingEventListener.of(f);
				})
				.collect(Collectors.toSet());
	}
}
package com.nowellpoint.console.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractNotifications {
	public static List<Notification> of(List<com.nowellpoint.console.entity.Notification> source) {
		return source == null ? Collections.emptyList() : source.stream()
				.map(f -> {
					return Notification.of(f);
				})
				.collect(Collectors.toList());
	}
}
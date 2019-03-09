package com.nowellpoint.console.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractUserLicenses {
	public static List<UserLicense> of(Set<com.nowellpoint.console.entity.UserLicense> source) {
		return source == null ? Collections.emptyList() : source.stream()
				.map(f -> UserLicense.of(f))
				.collect(Collectors.toList());
	}
	
	public static List<UserLicense> of(com.nowellpoint.client.sforce.model.UserLicense[] source) {
		return source == null ? Collections.emptyList() : Arrays.asList(source).stream()
				.map(f -> UserLicense.of(f))
				.collect(Collectors.toList());
	}
}
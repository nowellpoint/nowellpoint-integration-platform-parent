package com.nowellpoint.console.model;

import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractFeatures {
	public static List<Feature> of(List<com.nowellpoint.console.entity.Feature> source) {
		return source.stream()
				.map(f -> {
					return Feature.of(f);
				})
				.collect(Collectors.toList());
	}
}
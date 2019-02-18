package com.nowellpoint.console.model;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractTransactions {
	public static Set<Transaction> of(Set<com.nowellpoint.console.entity.Transaction> source) {
		return source == null ? Collections.emptySet() : source.stream()
				.map(f -> {
					return Transaction.of(f);
				})
				.collect(Collectors.toSet());
	}
}
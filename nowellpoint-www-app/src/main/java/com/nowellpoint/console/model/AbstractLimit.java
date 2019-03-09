package com.nowellpoint.console.model;

import java.math.BigDecimal;

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
	public Integer getMax() {
		return 0;
	}
	
	@Value.Default
	public Integer getAvailable() {
		return 0;
	}
	
	@Value.Derived
	public Integer getUsed() {
		return getMax() - getAvailable();
	}
	
	@Value.Derived
	public Double getPercentAvailable() {
		if (getMax() == 0) {
			return Double.valueOf(0.00);
		} else if (getAvailable() == 0) {
			return Double.valueOf(0.00);
		} else if (getAvailable() == getMax()) {
			return Double.valueOf(100.00);
		} else {
			return BigDecimal.valueOf(getAvailable())
					.divide(BigDecimal.valueOf(getMax()), 2, BigDecimal.ROUND_DOWN)
					.multiply(BigDecimal.valueOf(100))
					.doubleValue();
		}
	}
	
	public static Limit of (com.nowellpoint.client.sforce.model.Limit source) {
		return source == null ? Limit.builder().build() : Limit.builder()
				.max(source.getMax())
				.available(source.getRemaining())
				.build();
	}
	
	public static Limit of(com.nowellpoint.console.entity.Limit source) {
		return source == null ? Limit.builder().build() : Limit.builder()
				.max(source.getMax())
				.available(source.getAvailable())
				.build();
	}
}
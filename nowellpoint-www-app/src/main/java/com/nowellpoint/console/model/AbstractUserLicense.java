package com.nowellpoint.console.model;

import java.math.BigDecimal;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = UserLicense.class)
@JsonDeserialize(as = UserLicense.class)
public abstract class AbstractUserLicense {
	public abstract String getName();
	public abstract String getStatus();
	
	@Value.Default
	public Integer getMax() {
		return 0;
	}
	
	@Value.Default
	public Integer getUsed() {
		return 0;
	}
	
	@Value.Derived
	public Integer getAvailable() {
		return getMax() - getUsed();
	}
	
	@Value.Derived
	public Double getPercentAvailable() {
		if (getMax() == 0) {
			return Double.valueOf(0);
		} else if (getUsed() == 0) {
			return Double.valueOf(100);
		} else if (getAvailable() == 0) {
			return Double.valueOf(0);
		} else {
			return BigDecimal.valueOf(getAvailable())
					.divide(BigDecimal.valueOf(getMax()), 2, BigDecimal.ROUND_DOWN)
					.multiply(BigDecimal.valueOf(100))
					.doubleValue();
		}
	}
	
	public static UserLicense of(com.nowellpoint.console.entity.UserLicense source) {
		return source == null ? null : UserLicense.builder()
				.name(source.getName())
				.status(source.getStatus())
				.max(source.getTotalLicenses())
				.used(source.getUsedLicenses())
				.build();
	}
	
	public static UserLicense of (com.nowellpoint.client.sforce.model.UserLicense source) {
		return source == null ? null : UserLicense.builder()
				.name(source.getName())
				.status(source.getStatus())
				.max(source.getTotalLicenses())
				.used(source.getUsedLicenses())
				.build();
	}
}
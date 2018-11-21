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
	public Integer getTotalLicenses() {
		return 0;
	}
	
	@Value.Default
	public Integer getUsedLicenses() {
		return 0;
	}
	
	@Value.Derived
	public Integer getAvailableLicenses() {
		return getTotalLicenses() - getUsedLicenses();
	}
	
	@Value.Derived
	public Double getPercentAvailable() {
		if (getTotalLicenses() == 0) {
			return Double.valueOf(0);
		} else if (getUsedLicenses() == 0) {
			return Double.valueOf(100);
		} else if (getAvailableLicenses() == 0) {
			return Double.valueOf(0);
		} else {
			return BigDecimal.valueOf(getAvailableLicenses())
					.divide(BigDecimal.valueOf(getTotalLicenses()), 2, BigDecimal.ROUND_HALF_EVEN)
					.multiply(BigDecimal.valueOf(100))
					.doubleValue();
		}
	}
	
	public static UserLicense of(com.nowellpoint.console.entity.UserLicense source) {
		return source == null ? null : UserLicense.builder()
				.name(source.getName())
				.status(source.getStatus())
				.totalLicenses(source.getTotalLicenses())
				.usedLicenses(source.getUsedLicenses())
				.build();
	}
	
	public static UserLicense of (com.nowellpoint.client.sforce.model.UserLicense source) {
		return source == null ? null : UserLicense.builder()
				.name(source.getName())
				.status(source.getStatus())
				.totalLicenses(source.getTotalLicenses())
				.usedLicenses(source.getUsedLicenses())
				.build();
	}
}
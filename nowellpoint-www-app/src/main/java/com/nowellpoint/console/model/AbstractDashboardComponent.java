package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
public abstract class AbstractDashboardComponent {
	public static final String PERCENT = "PERCENT";
	public static final String AMOUNT = "AMOUNT";
	
	public abstract @Nullable String getUnit();
	
	@Value.Default
	public Double getValue() {
		return Double.valueOf(0);
	}
	
	@Value.Default
	public Double getDelta() {
		return Double.valueOf(0);
	}
	
	@Value.Derived
	public Double getAbsoluteValue() {
		return Math.abs(getValue());
	}
	
	public static DashboardComponent of(com.nowellpoint.console.entity.DashboardComponent source) {
		return source == null ? DashboardComponent.builder().build() : DashboardComponent.builder()
				.delta(source.getDelta())
				.unit(source.getUnit())
				.value(source.getValue())
				.build();
	}
}
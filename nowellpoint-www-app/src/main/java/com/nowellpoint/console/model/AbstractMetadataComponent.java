package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
public abstract class AbstractMetadataComponent {
	public static final String PERCENT = "PERCENT";
	public static final String AMOUNT = "AMOUNT";
	public static final String QUANTITY = "QUANTITY";
	
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
	
	public static MetadataComponent of(com.nowellpoint.console.entity.MetadataComponent source) {
		return source == null ? MetadataComponent.builder().build() : MetadataComponent.builder()
				.delta(source.getDelta())
				.unit(source.getUnit())
				.value(source.getValue())
				.build();
	}
}
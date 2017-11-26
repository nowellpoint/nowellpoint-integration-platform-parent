package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Feature.class)
@JsonDeserialize(as = Feature.class)
public abstract class AbstractFeature {
	public abstract Integer getSortOrder();
	public abstract String getCode();
	public abstract String getName();
	public abstract @Nullable String getDescription();
	public abstract @Nullable Boolean getEnabled();
	public abstract String getQuantity();
	
	public static Feature of(com.nowellpoint.api.model.document.Feature source) {
		Feature feature = ModifiableFeature.create()
				.setCode(source.getCode())
				.setDescription(source.getDescription())
				.setEnabled(source.getEnabled())
				.setName(source.getName())
				.setQuantity(source.getQuantity())
				.setSortOrder(source.getSortOrder())
				.toImmutable();
		
		return feature;
	}
}
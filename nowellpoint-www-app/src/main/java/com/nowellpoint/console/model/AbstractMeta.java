package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Meta.class)
@JsonDeserialize(as = Meta.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractMeta {
	public abstract @Nullable String getMethod();
	public abstract @Nullable String getAccepts();
	public abstract @Nullable String getProduces();
	public abstract @Nullable String getRel();
	
	public abstract @JsonIgnore @Nullable String getResourcePath();
	public abstract @JsonIgnore @Nullable String getId();
	
	public String getHref() {		
		return new StringBuilder().append(System.getProperty("hostname"))
				.append(getResourcePath())
				.toString()
				.replace(":id", getId());
	}
}
package com.nowellpoint.console.model;

import javax.annotation.Nullable;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.UriBuilder;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.JaxRsActivator;

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
	
	public abstract @JsonIgnore @Nullable Class<?> getResourceClass();
	public abstract @JsonIgnore @Nullable String getId();
	
	public String getHref() {		
		return UriBuilder.fromUri("https://localhost:8443")
				.path(JaxRsActivator.class.getAnnotation(ApplicationPath.class).value())
				.path(getResourceClass())
				.path("/{id}")
				.build(getId())
				.toString();
	}
}
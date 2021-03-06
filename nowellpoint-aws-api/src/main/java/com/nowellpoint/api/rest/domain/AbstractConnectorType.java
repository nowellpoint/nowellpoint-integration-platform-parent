package com.nowellpoint.api.rest.domain;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = ConnectorType.class)
@JsonDeserialize(as = ConnectorType.class)
public abstract class AbstractConnectorType {
	public abstract @JsonIgnore String getName();
	public abstract String getScheme();
	public abstract String getGrantType();
	public abstract @JsonGetter("typeName") String getDisplayName();
	public abstract String getAuthEndpoint();
	public abstract String getIconHref();
	public abstract Boolean getIsSandbox();
	public abstract @Nullable Set<Service> getServices();
	
	public static ConnectorType of(com.nowellpoint.api.model.document.ConnectorType source) {
		Set<Service> services = source.getServices().stream()
				.map(s -> Service.of(s))
				.collect(Collectors.toSet());
		
		ConnectorType instance = ConnectorType.builder()
				.authEndpoint(source.getAuthEndpoint())
				.displayName(source.getDisplayName())
				.grantType(source.getGrantType())
				.iconHref(source.getIconHref())
				.isSandbox(source.getIsSandbox())
				.name(source.getName())
				.scheme(source.getScheme())
				.services(services)
				.build();
		
		return instance;
	}
}
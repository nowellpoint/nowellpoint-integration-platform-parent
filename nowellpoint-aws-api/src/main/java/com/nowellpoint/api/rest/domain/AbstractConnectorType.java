package com.nowellpoint.api.rest.domain;

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
	
	public static ConnectorType of(com.nowellpoint.api.model.document.ConnectorType source) {
		ModifiableConnectorType connectorType = ModifiableConnectorType.create(); //modelMapper.map(source, ModifiableConnectorType.class);
		return connectorType.toImmutable();
	}
}
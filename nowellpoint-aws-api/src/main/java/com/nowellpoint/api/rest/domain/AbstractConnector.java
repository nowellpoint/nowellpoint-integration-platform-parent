package com.nowellpoint.api.rest.domain;

import java.util.Date;

import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.rest.ConnectorResource;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.mongodb.document.MongoDocument;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Connector.class)
@JsonDeserialize(as = Connector.class)
public abstract class AbstractConnector extends AbstractImmutableResource {
	public static final String NOT_CONNECTED = "Not Connected";
	public static final String CONNECTED = "Connected";
	public static final String DISCONNECTED = "Disconnected";
	public static final String FAILED_TO_CONNECT = "Failed to connect";
	
	public abstract String getName();
	public abstract @JsonUnwrapped ConnectorType getConnectorType();
	public abstract @Nullable String getUsername();
	public abstract @Nullable String getPassword();
	public abstract @Nullable String getClientId();
	public abstract @Nullable String getClientSecret();
	public abstract @Nullable String getConnectedAs();
	public abstract @Nullable Date getConnectedOn();
	
	@Value.Default
	public Boolean getIsConnected() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public String getStatus() {
		return NOT_CONNECTED;
	}
	
	@Value.Default
	public UserInfo getCreatedBy() {
		return UserInfo.of(ClaimsContext.getClaims());
	}
	
	@Value.Default
	public UserInfo getLastUpdatedBy() {
		return UserInfo.of(ClaimsContext.getClaims());
	}
	
	@Value.Default
	public OrganizationInfo getOwner() {
		return OrganizationInfo.of(ClaimsContext.getClaims());
	}
	
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(ConnectorResource.class)
				.build();
	}
	
	@Override
	public void fromDocument(MongoDocument source) {
		modelMapper.map(source, this);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(Connector.class);
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Connector.class);
	}
	
	public static Connector of(MongoDocument source) {
		ModifiableConnector connector = modelMapper.map(source, ModifiableConnector.class);
		return connector.toImmutable();
	}
}
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
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new", depluralizeDictionary = {"service:services"})
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
	public abstract @Nullable SalesforceMetadata getSalesforceMetadata();
	
	public static Connector of(com.nowellpoint.api.model.document.Connector source) {
		
		Connector instance = Connector.builder()
				.clientId(source.getClientId())
				.clientSecret(source.getClientSecret())
				.connectedAs(source.getConnectedAs())
				.connectedOn(source.getConnectedOn())
				.connectorType(ConnectorType.of(source.getConnectorType()))
				.createdBy(UserInfo.of(source.getCreatedBy()))
				.createdOn(source.getCreatedOn())
				.id(source.getId().toString())
				.isConnected(source.getIsConnected())
				.lastUpdatedBy(UserInfo.of(source.getLastUpdatedBy()))
				.lastUpdatedOn(source.getLastUpdatedOn())
				.name(source.getName())
				.owner(OrganizationInfo.of(source.getOwner()))
				.password(source.getPassword())
				.salesforceMetadata(SalesforceMetadata.of(source.getSalesforceMetadata()))
				.status(source.getStatus())
				.username(source.getUsername())
				.build();
		
		return instance;
	}
	
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
	public void replace(MongoDocument source) {
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
}
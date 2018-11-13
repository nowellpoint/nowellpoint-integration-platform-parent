package com.nowellpoint.console.model;

import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Connection.class)
@JsonDeserialize(as = Connection.class)
public abstract class AbstractConnection {
	public abstract String getId();
	public abstract String getAccessToken();
	public abstract String getInstanceUrl();
	public abstract String getRefreshToken();
	public abstract String getTokenType();
	public abstract String getIssuedAt();
	public abstract String getConnectedAs();
	public abstract Date getConnectedAt();
	
	public static final String CONNECTED = "Connected";
	public static final String NOT_CONNECTED = "Not Connected";
	
	@Value.Default
	public String getStatus() {
		return NOT_CONNECTED;
	}
	
	@Value.Default
	public Boolean getIsConnected() {
		return Boolean.FALSE;
	}
	
	public static Connection of(com.nowellpoint.console.entity.Connection entity) {
		return entity == null ? null : Connection.builder()
				.id(entity.getId())
				.accessToken(entity.getAccessToken())
				.instanceUrl(entity.getInstanceUrl())
				.refreshToken(entity.getRefreshToken())
				.tokenType(entity.getTokenType())
				.issuedAt(entity.getIssuedAt())
				.connectedAs(entity.getConnectedAs())
				.connectedAt(entity.getConnectedAt())
				.status(entity.getStatus())
				.isConnected(entity.getIsConnected())
				.build();
	}
	
}
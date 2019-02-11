package com.nowellpoint.console.model;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.service.ServiceClient;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Connection.class)
@JsonDeserialize(as = Connection.class)
public abstract class AbstractConnection {
	public abstract String getId();
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
	
	@Value.Derived
	public String getApiVersion() {
		return Salesforce.API_VERSION;
	}
	
	public static Connection of(com.nowellpoint.console.entity.Connection entity) {
		return entity == null ? null : Connection.builder()
				.id(entity.getId())
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
	
	public static Connection of(Token token) throws InterruptedException, ExecutionException {
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
		FutureTask<com.nowellpoint.client.sforce.model.Identity> getIdentityTask = new FutureTask<com.nowellpoint.client.sforce.model.Identity>(
				new Callable<com.nowellpoint.client.sforce.model.Identity>() {
					@Override
					public com.nowellpoint.client.sforce.model.Identity call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getIdentity(token);
				   }
				}
		);
		
		executor.execute(getIdentityTask);
		
		return Connection.builder()
				.connectedAs(getIdentityTask.get().getUsername())
				.connectedAt(Date.from(Instant.now()))
				.id(getIdentityTask.get().getId())
				.instanceUrl(token.getInstanceUrl())
				.isConnected(Boolean.TRUE)
				.issuedAt(token.getIssuedAt())
				.refreshToken(token.getRefreshToken())
				.status(Connection.CONNECTED)
				.tokenType(token.getTokenType())
				.build();
	}
}
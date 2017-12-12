package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractSalesforceCredentials {
	public abstract String getClientId();
	public abstract String getClientSecret();
	public abstract String getUsername();
	public abstract String getPassword();
	
	public String asString() {
		return new StringBuilder()
				.append(getClientId())
				.append(":")
				.append(getClientSecret())
				.append(":")
				.append(getUsername())
				.append(":")
				.append(getPassword())
				.toString();
	}
	
	public static SalesforceCredentials of(String credentialsString) {
		String[] values = credentialsString.split(":");
		
		SalesforceCredentials credentials = SalesforceCredentials.builder()
				.clientId(values[0])
				.clientSecret(values[1])
				.username(values[2])
				.password(values[3])
				.build();
		
		return credentials;
	}
}
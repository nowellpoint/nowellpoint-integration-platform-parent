package com.nowellpoint.client.sforce.test;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Account;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.util.SecretsManager;

public class TestDuplicateAccount {
	
	private static Token token;
	
	@BeforeClass
	public static void init() {
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();

		OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(request);
			
		token = response.getToken();
	}
	
	public void testGetSchema() {
		
		HttpResponse response = RestResource.get(token.getInstanceUrl())
				.path("services/data/v44.0/event/eventSchema/7JlOZq3jW-7XCxS5BxgExQ")
				.bearerAuthorization(token.getAccessToken())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
     			.queryParameter("payloadFormat", "COMPACT")
     			.execute();
		
		System.out.println(response.getStatusCode());
		System.out.println(response.getAsString());

	}
	
	@Test
	public void testUsernamePasswordAuthentication() {
		
		Salesforce client = SalesforceClientBuilder.defaultClient(token);
		
		String query = Account.QUERY.concat("Where BillingCountryCode != null Limit 10");
		
		Set<Account> accounts = client.query(Account.class, query);
		
		Account account = accounts.stream().findFirst().get();
		
		System.out.println(account.getName());
		System.out.println(account.getCreatedBy().getEmail());
		System.out.println(account.getLastModifiedBy().getEmail());
		System.out.println(account.getCreatedDate());
		System.out.println(account.getLastModifiedDate());
		
		String address = new StringBuilder()
				.append(account.getBillingStreet())
				.append("+")
				.append(account.getBillingCity())
				.append("+")
				.append(account.getBillingState())
				.toString();
		
		HttpResponse response = RestResource.get("https://maps.googleapis.com")
				.path("maps/api/geocode/json")
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
     			.queryParameter("address", address)
     			.queryParameter("key", "")
     			.execute();
		
		System.out.println(response.getStatusCode());
		System.out.println(response.getAsString());
		
	}
}
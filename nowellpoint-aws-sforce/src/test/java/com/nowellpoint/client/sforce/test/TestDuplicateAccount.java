package com.nowellpoint.client.sforce.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
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
	public void testGoogleGeocode() {
		
		Salesforce client = SalesforceClientBuilder.defaultClient(token);

		Account account = client.findById(Account.class, "0013000001Fc0b0AAB");
		
		String address = new StringBuilder()
				.append(account.getBillingStreet())
				.append(" ")
				.append(account.getBillingCity())
				.append(", ")
				.append(account.getBillingState())
				.append(" ")
				.append(account.getBillingPostalCode())
				.append(" ")
				.append(account.getBillingCountryCode())
				.toString();
		
		GeoApiContext context = new GeoApiContext.Builder()
				.apiKey(System.getenv("GOOGLE_API_KEY"))
			    .build();
		
		GeocodingResult[] results;
		try {
			results = GeocodingApi.geocode(context, address).await();
			
			System.out.println(new ObjectMapper().writeValueAsString((results[0])));
			
			Arrays.asList(results[0].addressComponents).forEach(ac -> {
				//ac.types
			});
			
		} catch (ApiException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGoogleGeocodeLimited() {
		
		Salesforce client = SalesforceClientBuilder.defaultClient(token);

		Account account = client.findById(Account.class, "0013000001Fc0guAAB");
		
		String address = new StringBuilder()
				.append(account.getShippingStreet())
				.append(" ")
				.append(account.getShippingCity())
				.append(", ")
				.append(account.getShippingState())
				.append(" ")
				.append(account.getShippingPostalCode())
				.append(" ")
				.append(account.getShippingCountryCode())
				.toString();
		
		GeoApiContext context = new GeoApiContext.Builder()
				.apiKey(System.getenv("GOOGLE_API_KEY"))
			    .build();
		
		GeocodingResult[] results;
		try {
			results = GeocodingApi.geocode(context, address).await();
			
			if (results.length > 0) {
				System.out.println(new ObjectMapper().writeValueAsString((results[0])));
			}
			
		} catch (ApiException | InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
}
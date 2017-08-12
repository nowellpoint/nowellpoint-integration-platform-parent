package com.nowellpoint.client.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.ClientCredentialsAuthenticator;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

public class TestAccountProfile {
	
	private static Token token;
	
	@BeforeClass
	public static void authenticate() {
		token = new ClientCredentialsAuthenticator().authenticate();
	}
	
	@Test
	public void testCreditCard() {
		
		Identity identity = NowellpointClient.defaultClient(token)
				.identity()
				.get(token.getId());
		
		CreditCardRequest createCreditCardRequest = new CreditCardRequest()
				.withOrganizationId(identity.getOrganization().getId())
				.withCardholderName("John Herson")
				.withExpirationMonth("12")
				.withExpirationYear("2018")
				.withNumber("4111111111111111")
				.withCvv("010")
				.withPrimary(Boolean.FALSE)
				.withCity("Raleigh")
				.withCountryCode("US")
				.withPostalCode("27601")
				.withState("NC")
				.withStreet("300 W. Hargett Street, Unit 415")
				.withFirstName("John")
				.withLastName("Herson");
		
		CreateResult<CreditCard> createResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.creditCard()
				.add(createCreditCardRequest);
		
		Assert.assertTrue(createResult.isSuccess());
		
		CreditCardRequest udpateCreditCardRequest = new CreditCardRequest()
				.withOrganizationId(identity.getOrganization().getId())
				.withToken(createResult.getTarget().getToken())
				.withCardholderName("John Herson")
				.withExpirationMonth("12")
				.withExpirationYear("2019")
				.withCvv("010")
				.withPrimary(Boolean.FALSE)
				.withCity("Raleigh")
				.withCountryCode("US")
				.withPostalCode("27601")
				.withState("NC")
				.withStreet("300 W. Hargett Street, Unit 415")
				.withFirstName("John")
				.withLastName("Herson");
		
		UpdateResult<CreditCard> updateResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.creditCard()
				.update(udpateCreditCardRequest);
		
		Assert.assertTrue(updateResult.isSuccess());
		
		DeleteResult deleteResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.creditCard()
				.delete(identity.getId(), updateResult.getTarget().getToken());
		
		Assert.assertTrue(deleteResult.isSuccess());
		
	}
	
	@AfterClass
	public static void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(token)
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
}
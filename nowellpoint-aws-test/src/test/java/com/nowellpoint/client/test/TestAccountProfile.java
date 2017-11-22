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
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Organization;
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
		
		CreditCardRequest creditCardRequest = CreditCardRequest.builder()
				.cardholderName("John Herson")
				.cvv("010")
				.expirationMonth("12")
				.expirationYear("2019")
				.number("1111111111111111")
				.organizationId(identity.getOrganization().getId())
				.build();
				
		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.creditCard()
				.update(creditCardRequest);
		
		Assert.assertTrue(updateResult.isSuccess());
		
		DeleteResult deleteResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.creditCard()
				.delete(identity.getId(), updateResult.getTarget().getSubscription().getCreditCard().getToken());
		
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
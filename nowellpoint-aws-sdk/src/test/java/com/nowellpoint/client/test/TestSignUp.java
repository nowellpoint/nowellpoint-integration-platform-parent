package com.nowellpoint.client.test;

import org.junit.Test;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.client.model.SignUpResult;
import com.nowellpoint.client.model.User;

public class TestSignUp {

	@Test
	public void testSignUp() {
		SignUpRequest signUpRequest = new SignUpRequest()
				.withFirstName("Test")
				.withLastName("User")
				.withEmail("jherson@aim.com")
				.withPassword("!t2U1&JUTJvY")
				.withConfirmPassword("!t2U1&JUTJvY")
				.withCountryCode("US")
				.withPlanId("57fa74d801936217bb99643e")
				.withCardNumber("4111111111111111")
				.withExpirationMonth("12")
				.withExpirationYear("2018")
				.withSecurityCode("123");
		
		SignUpResult<User> signUpResult = new NowellpointClient()
				.user()
				.signUp(signUpRequest);
		
		if (!signUpResult.isSuccess()) {
			System.out.println(signUpResult.getErrorMessage());
		} else {
			System.out.println(signUpResult.getTarget().getEmailVerificationToken());
			System.out.println(signUpResult.getTarget().getHref());
		}
	}
}
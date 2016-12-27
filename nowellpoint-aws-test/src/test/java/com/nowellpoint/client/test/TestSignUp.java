package com.nowellpoint.client.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.junit.Test;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.client.model.SignUpResult;
import com.nowellpoint.client.model.User;

public class TestSignUp {
	
	private static Logger log = Logger.getLogger(TestSignUp.class);

	@Test
	public void testSignUp() {
		
		GetPlansRequest getPlansRequest = new GetPlansRequest()
				.withLanguageSidKey("en_US")
				.withLocaleSidKey("en_US");
		
		GetResult<List<Plan>> getResult = new NowellpointClient()
				.plan()
				.getPlans(getPlansRequest);
		
		assertNotNull(getResult.getTarget());
		
		List<Plan> plans = getResult.getTarget().stream()
				.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
				.collect(Collectors.toList());
		
		plans.stream().forEach(plan -> {
			
			log.info("testing plan: " + plan.getPlanName());
			
			SignUpRequest signUpRequest = new SignUpRequest()
					.withFirstName("Test")
					.withLastName("User")
					.withEmail("test.nowellpoint@mailinator.com")
					.withPassword("!t2U1&JUTJvY")
					.withConfirmPassword("!t2U1&JUTJvY")
					.withCountryCode("US")
					.withPlanId(plan.getId())
					.withCardNumber("4111111111111111")
					.withExpirationMonth("12")
					.withExpirationYear("2018")
					.withSecurityCode("123");
			
			SignUpResult<User> signUpResult = new NowellpointClient()
					.user()
					.signUp(signUpRequest);
			
			assertTrue(signUpResult.isSuccess());
			assertNotNull(signUpResult.getTarget());
			assertNotNull(signUpResult.getTarget().getHref());
			assertNotNull(signUpResult.getTarget().getEmailVerificationToken());
			
			signUpResult = new NowellpointClient()
					.user()
					.verifyEmail(signUpResult.getTarget().getEmailVerificationToken());
			
			assertTrue(signUpResult.isSuccess());
		});
	}
}
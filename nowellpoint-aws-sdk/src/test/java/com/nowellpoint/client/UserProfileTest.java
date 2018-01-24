package com.nowellpoint.client;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.UserProfile;
import com.nowellpoint.client.model.UserProfileRequest;

public class UserProfileTest {
	
	private static Token token;
	
	@BeforeClass
	public static void authenticate() {
		PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
				.setUsername("jherson@aim.com")
				.setPassword("mypassw0rd")
				.build();

		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(passwordGrantRequest);

		token = oauthAuthenticationResponse.getToken();
		
		System.out.println(token.getAccessToken());
	}
	
	@Test
	public void updateUserProfile() {
		UserProfileRequest userProfileRequest = UserProfileRequest.builder()
				.token(token)
				.firstName("John")
				.lastName("Herson")
				.title("CEO")
				.email("jherson@aim.com")
				.phone("999-999-9999")
				.locale("en_us")
				.timeZone("America/New_York")
				.build();

		UpdateResult<UserProfile> updateResult = NowellpointClient.defaultClient(token)
				.userProfile()
				.update("59d592cf5e7a9bb2231a87ee", userProfileRequest);
		
		System.out.println(updateResult.isSuccess());
		System.out.println(updateResult.getTarget().getTitle());
		
		AddressRequest addressRequest = AddressRequest.builder()
				.city("Raleigh")
				.countryCode("US")
				.postalCode("27601")
				.state("NC")
				.street("129 S. Bloodworth Street")
				.token(token)
				.build();
		
		UpdateResult<UserProfile> addressUpdateResult = NowellpointClient.defaultClient(token)
				.userProfile()
				.address()
				.update("59d592cf5e7a9bb2231a87ee", addressRequest);
		
		System.out.println(addressUpdateResult.isSuccess());
	}
	
	@AfterClass
	public static void logout() {
		token.delete();
	}
}
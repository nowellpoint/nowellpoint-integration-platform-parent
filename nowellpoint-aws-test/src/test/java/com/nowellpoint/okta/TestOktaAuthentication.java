package com.nowellpoint.okta;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.model.TokenResponse;
import com.nowellpoint.client.model.TokenVerificationResponse;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.client.Clients;
import com.okta.sdk.clients.AuthApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.auth.AuthResult;
import com.okta.sdk.resource.ResourceException;
import com.okta.sdk.resource.group.Group;
import com.okta.sdk.resource.group.GroupProfile;
import com.okta.sdk.resource.user.PasswordCredential;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserCredentials;
import com.okta.sdk.resource.user.UserProfile;
import com.okta.sdk.resource.user.UserStatus;

public class TestOktaAuthentication {
	
	private static final String API_KEY = System.getenv("OKTA_API_KEY");
	private static final String ORG_URL = System.getenv("OKTA_ORG_URL");
	private static Client client;
	
	@BeforeClass
	public static void initializeClient() {
		
		ClientBuilder builder = Clients.builder();
		
		ClientCredentials<String> credentials = new TokenClientCredentials(API_KEY);
		
		client = builder.setClientCredentials(credentials)
				.setOrgUrl(ORG_URL)
				.build();
		
	}
	
	@Test
	public void testAuthenticate() {
		
		HttpResponse httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("v1")
				.path("token")
				.parameter("grant_type", "password")
				.parameter("username", System.getenv("NOWELLPOINT_USERNAME"))
				.parameter("password", System.getenv("NOWELLPOINT_PASSWORD"))
				.parameter("scope", "offline_access")
				.execute();
		
		if (httpResponse.getStatusCode() != 200) {
			System.out.println(httpResponse.getAsString());
			return;
		}
		
		TokenResponse token = httpResponse.getEntity(TokenResponse.class);
		
		assertNotNull(token.getAccessToken());
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getRefreshToken());
		assertNotNull(token.getScope());
		assertNotNull(token.getTokenType());
		
		long start = System.currentTimeMillis();
		
		httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("v1")
				.path("introspect")
				.parameter("token", "kdjfdkjfdkj")
				.parameter("token_type_hint", "access_token")
				.execute();
		
		System.out.println(System.currentTimeMillis() - start);
		
		TokenVerificationResponse verification = httpResponse.getEntity(TokenVerificationResponse.class);
		
		if (! verification.getGroups().isEmpty()) {
			System.out.println(verification.getGroups().get(0));
		}
		
		httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("v1")
				.path("revoke")
				.parameter("token", token.getAccessToken())
				.parameter("token_type_hint", "access_token")
				.execute();
		
		assertEquals(httpResponse.getStatusCode(), 200);
		
	}
	
	//@Test
	public void testCreateUser() {
		
		try {
			
			UserProfile userProfile = client.instantiate(UserProfile.class)
					.setFirstName("John")
					.setLastName("Herson")
					.setEmail("nowellpoint@mailinator.com")
					.setLogin("nowellpoint@mailinator.com");
			
			PasswordCredential passwordCredential = client.instantiate(PasswordCredential.class)
					.setValue("MuwKNl26k0Ee");
			
			UserCredentials userCredentials = client.instantiate(UserCredentials.class)
					.setPassword(passwordCredential);
			
			User user = client.instantiate(User.class)
					.setProfile(userProfile)
					.setStatus(UserStatus.ACTIVE)
					.setCredentials(userCredentials);
			
			user = client.createUser(user);
			
			GroupProfile groupProfile = client.instantiate(GroupProfile.class);
			groupProfile.setName("Test Group");
			
			Group group = client.instantiate(Group.class).setProfile(groupProfile);
			
			group = client.createGroup(group);
			
			client.getUser(user.getId()).addToGroup(group.getId());
			
			ApiClientConfiguration config = new ApiClientConfiguration(ORG_URL, API_KEY);
			
			AuthApiClient authApiClient = new AuthApiClient(config);		
			AuthResult result = authApiClient.authenticate("nowellpoint@mailinator.com", "MuwKNl26k0Ee", null);
			
			assertNotNull(result.getSessionToken());
			
			passwordCredential = client.instantiate(PasswordCredential.class)
					.setValue("MuwKNl26k0Ed");
			
			userCredentials = client.instantiate(UserCredentials.class)
					.setPassword(passwordCredential);
						
			userProfile = user.getProfile().setFirstName("Maou");
			
			user.setProfile(userProfile).setCredentials(userCredentials).update();
			
			client.getUser(user.getId()).deactivate();
			
			client.getUser(user.getId()).delete();
			
			client.getGroup(group.getId()).delete();
			
		} catch (ResourceException e) {
			System.out.println(e.getOktaError().getCode());
			System.out.println(e.getOktaError().getMessage());
			System.out.println(e.getOktaError().getStatus());
			System.out.println(e.getCode());
			System.out.println(e.getId());
			e.getOktaError().getCauses().forEach(error -> {
				System.out.println(error.getSummary());
			});
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
package com.nowellpoint.aws.idp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.nowellpoint.aws.idp.client.IdentityProviderClient;
import com.nowellpoint.aws.model.ClientException;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.PropertyStore;
import com.nowellpoint.aws.idp.model.CreateAccountRequest;
import com.nowellpoint.aws.idp.model.CreateAccountResponse;
import com.nowellpoint.aws.idp.model.DeleteAccountRequest;
import com.nowellpoint.aws.idp.model.DeleteAccountResponse;
import com.nowellpoint.aws.idp.model.GetAccountRequest;
import com.nowellpoint.aws.idp.model.GetAccountResponse;
import com.nowellpoint.aws.idp.model.GetCustomDataRequest;
import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.idp.model.RefreshTokenRequest;
import com.nowellpoint.aws.idp.model.RefreshTokenResponse;
import com.nowellpoint.aws.idp.model.RevokeTokenRequest;
import com.nowellpoint.aws.idp.model.RevokeTokenResponse;
import com.nowellpoint.aws.idp.model.SearchAccountRequest;
import com.nowellpoint.aws.idp.model.SearchAccountResponse;
import com.nowellpoint.aws.idp.model.UpdateAccountRequest;
import com.nowellpoint.aws.idp.model.UpdateAccountResponse;
import com.nowellpoint.aws.idp.model.VerifyTokenRequest;
import com.nowellpoint.aws.idp.model.VerifyTokenResponse;
import com.nowellpoint.aws.tools.TokenParser;

public class TestIdentityProviderClient {
	
	private Logger log = Logger.getLogger(TestIdentityProviderClient.class);
	
	private static IdentityProviderClient client = new IdentityProviderClient();
	
	@BeforeClass
	public static void before() {
		Properties.setSystemProperties(PropertyStore.SANDBOX);
	}
	
	@Test(expected = ClientException.class)
	public void testValidateCreateAccountRequest() {
		client.account(new CreateAccountRequest());
	}
	
	@Test(expected = ClientException.class)
	public void testValidateGetAccountRequest() {
		client.account(new GetAccountRequest());
	}
	
	@Test(expected = ClientException.class) 
	public void testValidateGetCustomDataRequest() {
		client.account(new GetCustomDataRequest());
	}
	
	@Test(expected = ClientException.class) 
	public void testValidateGetTokenRequest() {
		client.token(new GetTokenRequest());
	}
	
	@Test(expected = ClientException.class) 
	public void testValidateRefreshTokenRequest() {
		client.token(new RefreshTokenRequest());
	}
	
	@Test(expected = ClientException.class) 
	public void testValidateSearchAccountRequest() {
		client.account(new SearchAccountRequest());
	}
	
	@Test(expected = ClientException.class) 
	public void testValidateUpdateAccountRequest() {
		client.account(new UpdateAccountRequest());
	}
	
	@Test(expected = ClientException.class) 
	public void testValidateVerifyTokentRequest() {
		client.token(new VerifyTokenRequest());
	}
	
	@Test
	public void testCreateDeleteAccount() {
		
		log.info("testCreateDeleteAccount");
		
		CreateAccountRequest createAccountRequest = new CreateAccountRequest()
				.withDirectoryId(System.getProperty(Properties.STORMPATH_DIRECTORY_ID))
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withEmail("john.herson@nowellpoint.com")
				.withGivenName("Happy")
				.withMiddleName("D")
				.withSurname("Birthday")
				.withStatus("ENABLED")
				.withUsername("john.herson@nowellpoint.com")
				.withPassword("lsi38918902Js10");
		
		CreateAccountResponse createAccountResponse = client.account(createAccountRequest);
		
		assertTrue(createAccountResponse.getStatusCode() == 201);
		assertNotNull(createAccountResponse.getAccount().getHref());
		
		log.info("href: " + createAccountResponse.getAccount().getHref());
		
		DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest()
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withHref(createAccountResponse.getAccount().getHref());
		
		DeleteAccountResponse deleteAccountResponse = client.account(deleteAccountRequest);
		
		assertTrue(deleteAccountResponse.getStatusCode() == 204);	
	}

	@Test
	@Ignore
	public void testAuthenticateSuccess() {
		
		long start;
		
		System.out.println("get token test");
		
		start = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest()
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApplicationId(System.getProperty(Properties.STORMPATH_APPLICATION_ID))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		GetTokenResponse tokenResponse = client.token(tokenRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
		assertTrue(tokenResponse.getStatusCode() == 200);
		assertNotNull(tokenResponse.getToken().getAccessToken());
		assertNotNull(tokenResponse.getToken().getExpiresIn());
		assertNotNull(tokenResponse.getToken().getStormpathAccessTokenHref());
		assertNotNull(tokenResponse.getToken().getRefreshToken());
		assertNotNull(tokenResponse.getToken().getTokenType());
		
		System.out.println("verify token test");
		
		start = System.currentTimeMillis();
		
		VerifyTokenRequest verifyTokenRequest = new VerifyTokenRequest()
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApplicationId(System.getProperty(Properties.STORMPATH_APPLICATION_ID))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withAccessToken(tokenResponse.getToken().getAccessToken());
		
		VerifyTokenResponse verifyTokenResponse = client.token(verifyTokenRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
		assertTrue(verifyTokenResponse.getStatusCode() == 200);
		assertNotNull(verifyTokenResponse.getAuthToken());
		
		System.out.println("get account test");
		
		start = System.currentTimeMillis();
		
		String href = TokenParser.parseToken(System.getenv("STORMPATH_API_KEY_SECRET"), tokenResponse.getToken().getAccessToken());
		
		GetAccountRequest getAccountRequest = new GetAccountRequest()
				.withApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.withApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.withHref(href);
		
		GetAccountResponse getAccountResponse = client.account(getAccountRequest);
		
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
		
		assertTrue(getAccountResponse.getStatusCode() == 200);
		assertNotNull(getAccountResponse.getAccount());
		assertNotNull(getAccountResponse.getAccount().getEmail());
		assertNotNull(getAccountResponse.getAccount().getFullName());
		assertNotNull(getAccountResponse.getAccount().getGivenName());
		assertNotNull(getAccountResponse.getAccount().getHref());		
		assertNotNull(getAccountResponse.getAccount().getStatus());
		assertNotNull(getAccountResponse.getAccount().getSurname());
		
		System.out.println("refresh token test");
		
		start = System.currentTimeMillis();
		
		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest()
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApplicationId(System.getProperty(Properties.STORMPATH_APPLICATION_ID))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withRefreshToken(tokenResponse.getToken().getRefreshToken());
		
		RefreshTokenResponse refreshTokenResponse = client.token(refreshTokenRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
		assertTrue(refreshTokenResponse.getStatusCode() == 200);
		assertNotNull(refreshTokenResponse.getToken());
		
		System.out.println("revoke token test");
		
		start = System.currentTimeMillis();
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest()
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withAccessToken(refreshTokenResponse.getToken().getAccessToken());
		
		RevokeTokenResponse revokeTokenResponse = client.token(revokeTokenRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
		assertTrue(revokeTokenResponse.getStatusCode() == 204);
	}
	
	@Test
	@Ignore
	public void testSearchAccountAndUpdate() {
		
		long start;
		
        System.out.println("search account test");
        
        start = System.currentTimeMillis();
		
		SearchAccountRequest searchAccountRequest = new SearchAccountRequest()
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withDirectoryId(System.getProperty(Properties.STORMPATH_DIRECTORY_ID))
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withUsername("john.d.herson@gmail.com");
		
		SearchAccountResponse searchAccountResponse = client.account(searchAccountRequest);
		
		assertTrue(searchAccountResponse.getStatusCode() == 200);
		assertTrue(searchAccountResponse.getSize() == 1);
		assertNotNull(searchAccountResponse.getItems());
		assertTrue(searchAccountResponse.getItems().size() == 1);
		assertNotNull(searchAccountResponse.getItems().get(0).getHref());
		assertNotNull(searchAccountResponse.getItems().get(0).getEmail());
		assertNotNull(searchAccountResponse.getItems().get(0).getFullName());
		assertNotNull(searchAccountResponse.getItems().get(0).getGivenName());		
		assertNotNull(searchAccountResponse.getItems().get(0).getStatus());
		assertNotNull(searchAccountResponse.getItems().get(0).getSurname());
		
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
		
		System.out.println("udpate account test");
		
		start = System.currentTimeMillis();
		
		UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest()
				.withApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.withApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.withMiddleName("New")
				.withHref(searchAccountResponse.getItems().get(0).getHref());
		
		UpdateAccountResponse updateAccountResponse = client.account(updateAccountRequest);
		
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));

		assertTrue(updateAccountResponse.getStatusCode() == 200);
		assertNotNull(updateAccountResponse.getAccount());
		assertNotNull(updateAccountResponse.getAccount().getEmail());
		assertNotNull(updateAccountResponse.getAccount().getFullName());
		assertNotNull(updateAccountResponse.getAccount().getGivenName());
		assertNotNull(updateAccountResponse.getAccount().getHref());		
		assertNotNull(updateAccountResponse.getAccount().getStatus());
		assertNotNull(updateAccountResponse.getAccount().getSurname());
		
        start = System.currentTimeMillis();
        
        System.out.println("get account test");
		
		GetAccountRequest getAccountRequest = new GetAccountRequest().withApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.withApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.withHref(searchAccountResponse.getItems().get(0).getHref());
		
		GetAccountResponse getAccountResponse = client.account(getAccountRequest);
		
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
		
		assertTrue(getAccountResponse.getStatusCode() == 200);
		assertNotNull(getAccountResponse.getAccount());
		assertNotNull(getAccountResponse.getAccount().getEmail());
		assertNotNull(getAccountResponse.getAccount().getFullName());
		assertNotNull(getAccountResponse.getAccount().getGivenName());
		assertNotNull(getAccountResponse.getAccount().getHref());		
		assertNotNull(getAccountResponse.getAccount().getStatus());
		assertNotNull(getAccountResponse.getAccount().getSurname());
	}
}
package com.nowellpoint.okta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.okta.sdk.resource.ResourceException;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserBuilder;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;

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
	
	//@Test
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
		
		assertNotNull(token);
		assertNotNull(token.getAccessToken());
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getRefreshToken());
		assertNotNull(token.getScope());
		assertNotNull(token.getTokenType());
		
		final String modulusString = null;
		final String exponentString = null;
		
        SigningKeyResolver resolver = new SigningKeyResolverAdapter() {
        	@SuppressWarnings("rawtypes")
			public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
                try {
                    BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulusString));
                    BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentString));

                    return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };

        try {
            Jws<Claims> jwsClaims = Jwts.parser()
                .setSigningKeyResolver(resolver)
                .parseClaimsJws(token.getAccessToken());

            System.out.println("Verified Access Token");
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jwsClaims));
            
            String compactJws = Jwts.builder()
            		.setHeaderParam("kid", jwsClaims.getHeader().get("kid"))
            		.setId(jwsClaims.getBody().getId())
            		.setIssuer(jwsClaims.getBody().getIssuer())
            		.setAudience("organizationId")
            		.setSubject("userId")
            		.setExpiration(jwsClaims.getBody().getExpiration())
            		.setIssuedAt(jwsClaims.getBody().getIssuedAt())
            		.claim("scope", jwsClaims.getBody().get("groups"))
            		.signWith(SignatureAlgorithm.HS512, jwsClaims.getHeader().get("kid").toString())
            		.compact();
            
            System.out.println(compactJws);
            
            jwsClaims = Jwts.parser()
                    .setSigningKey(jwsClaims.getHeader().get("kid").toString())
                    .parseClaimsJws(compactJws);

                System.out.println("Verified Access Token");
                mapper = new ObjectMapper();
                System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jwsClaims));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		long start = System.currentTimeMillis();
		
		httpResponse = RestResource.post(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.basicAuthorization(System.getenv("OKTA_CLIENT_ID"), System.getenv("OKTA_CLIENT_SECRET"))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("v1")
				.path("token")
				.parameter("grant_type", "refresh_token")
				.parameter("refresh_token", token.getRefreshToken())
				.parameter("scope", "offline_access")
				.execute();
		
		token = httpResponse.getEntity(TokenResponse.class);
		
		assertNotNull(token);
		assertNotNull(token.getAccessToken());
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getRefreshToken());
		assertNotNull(token.getScope());
		assertNotNull(token.getTokenType());
		
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
	
	@Test
	public void testCreateUser() {
		
		try {
			
			User user = UserBuilder.instance()
					.setEmail("nowellpoint@mailinator.com")
					.setLogin("nowellpoint@mailinator.com")
					.setFirstName("John")
					.setLastName("Herson")
					.addGroup("00gax53y4htYrgYWB0h7")
					.buildAndCreate(client);
			
//			
//			ApiClientConfiguration config = new ApiClientConfiguration(ORG_URL, API_KEY);
//			
//			AuthApiClient authApiClient = new AuthApiClient(config);		
//			AuthResult result = authApiClient.authenticate("nowellpoint@mailinator.com", "MuwKNl26k0Ee", null);
//			
//			assertNotNull(result.getSessionToken());
//			
//			passwordCredential = client.instantiate(PasswordCredential.class)
//					.setValue("MuwKNl26k0Ed");
//			
//			userCredentials = client.instantiate(UserCredentials.class)
//					.setPassword(passwordCredential);
//						
//			userProfile = user.getProfile().setFirstName("Maou");
//			
//			user.setProfile(userProfile).setCredentials(userCredentials).update();
			
			client.getUser(user.getId()).deactivate();
			
			client.getUser(user.getId()).delete();
			
			//client.getGroup(group.getId()).delete();
			
		} catch (ResourceException e) {
			System.out.println(e.getOktaError().getCode());
			System.out.println(e.getOktaError().getMessage());
			System.out.println(e.getOktaError().getStatus());
			System.out.println(e.getCode());
			System.out.println(e.getId());
			e.getOktaError().getCauses().forEach(error -> {
				System.out.println(error.getSummary());
			});
		} //catch (IOException e) {
			//e.printStackTrace();
		//}	
	}
}
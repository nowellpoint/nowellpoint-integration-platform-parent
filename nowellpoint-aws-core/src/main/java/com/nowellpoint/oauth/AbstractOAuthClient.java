package com.nowellpoint.oauth;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.immutables.value.Value;

import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.oauth.model.OAuthClientException;
import com.nowellpoint.oauth.model.AuthenticationRequest;
import com.nowellpoint.oauth.model.ErrorResponse;
import com.nowellpoint.oauth.model.Key;
import com.nowellpoint.oauth.model.Keys;
import com.nowellpoint.oauth.model.OAuthProviderType;
import com.nowellpoint.oauth.model.TokenResponse;
import com.nowellpoint.oauth.model.TokenVerificationResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolverAdapter;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractOAuthClient {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractOAuthClient.class.getName());
	private static final Map<String,Key> KEY_CACHE = new ConcurrentHashMap<String,Key>();
	
	private static final String VERSION = "v1";
	private static final String TOKEN = "token";
	private static final String REVOKE = "revoke";
	private static final String INTROSPECT = "introspect";
	private static final String KEYS = "keys";
	private static final String GRANT_TYPE = "grant_type";
	//private static final String CLIENT_CREDENTIALS = "client_credentials";
	private static final String TOKEN_TYPE_HINT = "token_type_hint";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String SCOPE = "scope";
	private static final String OFFLINE_ACCESS = "offline_access";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	
	public abstract OAuthProviderType getProvider();
	
	public TokenResponse authenticate(AuthenticationRequest request) {	
		HttpResponse httpResponse = RestResource.post(getProvider().getAuthorizationServer())
				.basicAuthorization(getProvider().getClientId(), getProvider().getClientSecret())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(TOKEN)
				.parameter(GRANT_TYPE, PASSWORD)
				.parameter(SCOPE, OFFLINE_ACCESS)
				.parameter(USERNAME, request.getUsername())
				.parameter(PASSWORD, request.getPassword())
				.execute();
		
		TokenResponse response = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			response = httpResponse.getEntity(TokenResponse.class);
		} else {
			ErrorResponse error = httpResponse.getEntity(ErrorResponse.class);
			throw new OAuthClientException(error);
		}
		
		return response;
	}
	
	public TokenResponse refresh(String refreshToken) {		
		HttpResponse httpResponse = RestResource.post(getProvider().getAuthorizationServer())
				.basicAuthorization(getProvider().getClientId(), getProvider().getClientSecret())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(TOKEN)
				.parameter(GRANT_TYPE, REFRESH_TOKEN)
				.parameter(REFRESH_TOKEN, refreshToken)
				.parameter(SCOPE, OFFLINE_ACCESS)
				.execute();
		
		TokenResponse response = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			response = httpResponse.getEntity(TokenResponse.class);
		} else {
			ErrorResponse error = httpResponse.getEntity(ErrorResponse.class);	
			throw new OAuthClientException(error);
		}
		
		return response;
	}
	
	public void revoke(String accessToken) {
		HttpResponse httpResponse = RestResource.post(getProvider().getAuthorizationServer())
				.basicAuthorization(getProvider().getClientId(), getProvider().getClientSecret())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(REVOKE)
				.parameter(TOKEN, accessToken)
				.parameter(TOKEN_TYPE_HINT, ACCESS_TOKEN)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			ErrorResponse error = httpResponse.getEntity(ErrorResponse.class);	
			throw new OAuthClientException(error);
		}
	}
	
	public TokenVerificationResponse verify(String accessToken) {		
		HttpResponse httpResponse = RestResource.post(getProvider().getAuthorizationServer())
				.basicAuthorization(getProvider().getClientId(), getProvider().getClientSecret())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(INTROSPECT)
				.parameter(TOKEN, accessToken)
				.parameter(TOKEN_TYPE_HINT, ACCESS_TOKEN)
				.execute();
		
		return httpResponse.getEntity(TokenVerificationResponse.class);
	}
	
	public Jws<Claims> getClaims(String accessToken) {
		return Jwts.parser()
				.setSigningKeyResolver(new SigningKeyResolverAdapter() {
					@SuppressWarnings("rawtypes")
					public java.security.Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
						Key key = getKey(jwsHeader.getKeyId());
						try {
							BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getModulus()));
							BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getExponent()));
							return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
						} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
							LOGGER.log(Level.SEVERE, "Uncaught Exception", e);
							return null;
						}
					}})
				.parseClaimsJws(accessToken);
	}
	
	private Key getKey(String keyId) {
		if (! KEY_CACHE.containsKey(keyId)) {
			addKeys();
		}
		
		Key key = KEY_CACHE.get(keyId);
		
		return key;
	}
	
	private void addKeys() {
		
		Keys keys = getKeys();
		
		keys.getKeys().forEach(key -> {
			KEY_CACHE.put(key.getKeyId(), key);
		});
	}
	
	private Keys getKeys() {
		HttpResponse httpResponse = RestResource.get(getProvider().getAuthorizationServer())
				.basicAuthorization(getProvider().getClientId(), getProvider().getClientSecret())
				.accept(MediaType.APPLICATION_JSON)
				.path(VERSION)
				.path(KEYS)
				.execute();
		
		Keys keys = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			keys = httpResponse.getEntity(Keys.class);
		} else {
			ErrorResponse error = httpResponse.getEntity(ErrorResponse.class);	
			throw new OAuthClientException(error);
		}
		
		return keys;
	}
}
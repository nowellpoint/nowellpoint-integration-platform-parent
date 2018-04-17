package com.nowellpoint.console.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.InternalServerErrorException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.entity.IdentityDAO;
import com.nowellpoint.console.entity.IdentityDocument;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.www.app.util.EnvironmentVariables;
import com.nowellpoint.www.app.view.AuthenticationController;

import freemarker.log.Logger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import spark.Request;
import spark.Response;

public class AuthenticationService {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
	private static final Map<String,Key> KEY_CACHE = new ConcurrentHashMap<String,Key>();
	private static final String AUTH_TOKEN = "com.nowellpoint.auth.token";
	private static final String REDIRECT_URI = "redirect_uri";
	
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
	
	private IdentityDAO identityDAO;
	
	public AuthenticationService(Datastore datastore) {
		identityDAO = new IdentityDAO(IdentityDocument.class, datastore);
	}
	
	public String authentication(Request request, Response response) {
		String username = request.queryParams("username");
		String password = request.queryParams("password");
		
		TokenResponse tokenResponse = authenticate(username, password);
		
		Jws<Claims> claims = parseToken(tokenResponse.getAccessToken());
		
		LOGGER.info(claims.getBody().getSubject());

		//Token token = authenticate(username, password);
		
		Query<IdentityDocument> query = identityDAO.getDatastore()
				.createQuery(IdentityDocument.class)
				.field("providerId")
				.equal(claims.getBody().getSubject());
		
		IdentityDocument document = identityDAO.findOne(query);
		
		LOGGER.info("1: " + document.getId().toString());

		Long expiresIn = tokenResponse.getExpiresIn();

		try {
			response.cookie(AUTH_TOKEN, new ObjectMapper().writeValueAsString(tokenResponse), expiresIn.intValue(), true);
		} catch (IOException e) {
			throw new InternalServerErrorException(e);
		}
		
		return "";
		
	}

	private TokenResponse authenticate(String username, String password) {	
		HttpResponse httpResponse = RestResource.post(EnvironmentVariables.getOktaAuthorizationServer())
				.basicAuthorization(EnvironmentVariables.getOktaClientId(), EnvironmentVariables.getOktaClientSecret())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(VERSION)
				.path(TOKEN)
				.parameter(GRANT_TYPE, PASSWORD)
				.parameter(SCOPE, OFFLINE_ACCESS)
				.parameter(USERNAME, username)
				.parameter(PASSWORD, password)
				.execute();
		
		TokenResponse response = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			response = httpResponse.getEntity(TokenResponse.class);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			LOGGER.debug(error.getError());
			LOGGER.debug(error.getErrorDescription());
			throw new AuthenticationException(error.getError(), error.getErrorDescription());
		}
		
		return response;
	}
	
	private Jws<Claims> parseToken(String accessToken) {
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
							LOGGER.error(e.getMessage(), e);
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
		HttpResponse httpResponse = RestResource.get(EnvironmentVariables.getOktaAuthorizationServer())
				.basicAuthorization(EnvironmentVariables.getOktaClientId(), EnvironmentVariables.getOktaClientSecret())
				.accept(MediaType.APPLICATION_JSON)
				.path(VERSION)
				.path(KEYS)
				.execute();
		
		Keys keys = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			keys = httpResponse.getEntity(Keys.class);
		} else {
			Error error = httpResponse.getEntity(Error.class);	
			LOGGER.debug(error.getError());
			LOGGER.debug(error.getErrorDescription());
			throw new AuthenticationException(error.getError(), error.getErrorDescription());
		}
		
		//return keys;
//		Keys keys = authenticationService.getKeys();
		keys.getKeys().forEach(key -> {
			KEY_CACHE.put(key.getKeyId(), key);
		});
	}
}
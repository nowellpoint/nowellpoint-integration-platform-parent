package com.nowellpoint.www.app.view;

import static spark.Spark.halt;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.InternalServerErrorException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.console.entity.IdentityDAO;
import com.nowellpoint.console.entity.IdentityDocument;
import com.nowellpoint.console.service.AuthenticationException;
import com.nowellpoint.console.service.Error;
import com.nowellpoint.console.service.Key;
import com.nowellpoint.console.service.Keys;
import com.nowellpoint.console.service.TokenResponse;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.www.app.util.EnvironmentVariables;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import spark.Request;
import spark.Response;

public class AuthenticationController extends AbstractStaticController {
	
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
	
	public static class Template {
		public static final String LOGIN = "login.html";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	
	public static void verify(Configuration configuration, Request request, Response response) throws JsonParseException, JsonMappingException, IOException {
		Optional<String> cookie = Optional.ofNullable(request.cookie(AUTH_TOKEN));

		if (cookie.isPresent()) {

			Token token = objectMapper.readValue(cookie.get(), Token.class);

			request.attribute(AUTH_TOKEN, token);

			Identity identity = NowellpointClient.defaultClient(token)
					.identity()
					.get(token.getId());

			request.attribute("com.nowellpoint.auth.identity", identity);
			request.attribute("com.nowellpoint.default.locale", getDefaultLocale(identity));
			request.attribute("com.nowellpoint.default.timezone", getDefaultTimeZone(identity));

		} else {
			
			response.redirect(Path.Route.LOGIN.concat("?")
					.concat(REDIRECT_URI).concat("=")
					.concat(URLEncoder.encode(request.pathInfo(), "UTF-8")));
			
			halt();
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String serveLoginPage(Configuration configuration, Request request, Response response) {
		Map<String, Object> model = getModel();
		model.put(REDIRECT_URI, request.queryParams(REDIRECT_URI));
        return render(AuthenticationController.class, configuration, request, response, model, Template.LOGIN);
    };
    
    /**
     * 
     * @param configuration
     * @param request
     * @param response
     * @return
     */
    
	public static String login(Datastore datastore, Configuration configuration, Request request, Response response) {

		String username = request.queryParams("username");
		String password = request.queryParams("password");

		try {
//			PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
//					.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
//					.setUsername(username).setPassword(password).build();
//
//			OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
//					.authenticate(passwordGrantRequest);

			request.session().invalidate();
			
			TokenResponse tokenResponse = authenticate(username, password);
			
			Jws<Claims> claims = parseToken(tokenResponse.getAccessToken());
			
			LOGGER.info(claims.getBody().getSubject());

			//Token token = authenticate(username, password);
			
			IdentityDAO identityDAO = new IdentityDAO(IdentityDocument.class, datastore);
			
			Query<IdentityDocument> query = datastore.createQuery(IdentityDocument.class)
					.field("providerId")
					.equal(claims.getBody().getSubject());
			
			IdentityDocument document = identityDAO.findOne(query);
			
			LOGGER.info(document.getId().toString());

			Long expiresIn = tokenResponse.getExpiresIn();

			try {
				response.cookie(AUTH_TOKEN, objectMapper.writeValueAsString(tokenResponse), expiresIn.intValue(), true);
			} catch (IOException e) {
				throw new InternalServerErrorException(e);
			}

		} catch (IllegalArgumentException e) {

			Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", e.getMessage());
			return render(AuthenticationController.class, configuration, request, response, model, Template.LOGIN);

		} catch (OauthException e) {

			String acceptLanguages = request.headers("Accept-Language");

			LOGGER.info(acceptLanguages);

			String[] languages = acceptLanguages.trim().replace("-", "_").split(";");

			String[] locales = languages[0].split(",");

			LOGGER.info(locales[0]);

			Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", e.getErrorDescription());
			return render(AuthenticationController.class, configuration, request, response, model, Template.LOGIN);

		} catch (ServiceUnavailableException e) {
			
			LOGGER.error(e.getMessage());
			throw new InternalServerErrorException(e.getMessage());
		}

		if (request.queryParams(REDIRECT_URI) != null && !request.queryParams(REDIRECT_URI).isEmpty()) {
			response.redirect(request.queryParams(REDIRECT_URI));
		} else {
			response.redirect(Path.Route.START);
		}

		return "";
	};
    
    /**
     * 
     * @param configuration
     * @param request
     * @param response
     * @return
     */
	
	public static String logout(Configuration configuration, Request request, Response response) {

		Optional<String> cookie = Optional.ofNullable(request.cookie(AUTH_TOKEN));

		if (cookie.isPresent()) {

			try {
				Token token = objectMapper.readValue(cookie.get(), Token.class);
				token.delete();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}

			response.removeCookie(AUTH_TOKEN);
		}

		request.session().invalidate();

		response.redirect(Path.Route.INDEX);

		return "";
	};
	
	/**
	 * 
	 * @param configuration
	 * @param identity
	 * @return
	 */
	
	private static TimeZone getDefaultTimeZone(Identity identity) {		
		TimeZone timeZone = null;
		if (identity != null && identity.getTimeZone() != null) {
			timeZone = identity.getTimeZone();
		} else {
			timeZone = TimeZone.getDefault();
		}
		
		return timeZone;
	}
	
	/**
	 * 
	 * @param configuration
	 * @param identity
	 * @return
	 */
	
	private static Locale getDefaultLocale(Identity identity) {
		Locale locale = null;
		if (identity != null && identity.getLocale() != null) {
			locale = identity.getLocale();
		} else {
			locale = Locale.getDefault();
		}
		
		return locale;
	}
	
	private static TokenResponse authenticate(String username, String password) {	
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
	
	private static Jws<Claims> parseToken(String accessToken) {
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
	
	private static Key getKey(String keyId) {
		if (! KEY_CACHE.containsKey(keyId)) {
			addKeys();
		}
		
		Key key = KEY_CACHE.get(keyId);
		
		return key;
	}
	
	private static void addKeys() {
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
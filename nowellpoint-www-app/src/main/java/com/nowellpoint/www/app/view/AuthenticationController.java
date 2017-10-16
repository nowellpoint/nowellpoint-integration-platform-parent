package com.nowellpoint.www.app.view;

import static spark.Spark.halt;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class AuthenticationController extends AbstractStaticController {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
	private static final String AUTH_TOKEN = "com.nowellpoint.auth.token";
	private static final String REDIRECT_URI = "redirect_uri";
	
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

			Identity identity = NowellpointClient.defaultClient(token).identity().get(token.getId());

			request.attribute("com.nowellpoint.auth.identity", identity);
			request.attribute("com.nowellpoint.default.locale", getDefaultLocale(identity));
			request.attribute("com.nowellpoint.default.timezone", getDefaultTimeZone(identity));

		} else {
			response.redirect(Path.Route.LOGIN.concat("?").concat(REDIRECT_URI).concat("=")
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
    
	public static String login(Configuration configuration, Request request, Response response) {

		String username = request.queryParams("username");
		String password = request.queryParams("password");

		try {
			PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
					.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
					.setUsername(username).setPassword(password).build();

			OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(passwordGrantRequest);

			request.session().invalidate();

			Token token = oauthAuthenticationResponse.getToken();

			Long expiresIn = token.getExpiresIn();

			try {
				response.cookie(AUTH_TOKEN, objectMapper.writeValueAsString(token), expiresIn.intValue(), true);
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
			model.put("errorMessage", e.getMessage());
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
}
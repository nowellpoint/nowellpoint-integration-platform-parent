package com.nowellpoint.www.app.view;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.exception;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class AuthenticationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
	private static final String AUTH_TOKEN = "com.nowellpoint.auth.token";
	private static final String REDIRECT_URI = "redirect_uri";
	
	public static class Template {
		public static final String LOGIN = "login.html";
	}
	
	public AuthenticationController() {
		super(AuthenticationController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		before("/app/*", (request, response) -> verify(configuration, request, response));
		get(Path.Route.LOGIN, (request, response) -> showLoginPage(configuration, request, response));
        post(Path.Route.LOGIN, (request, response) -> login(configuration, request, response));
        get(Path.Route.LOGOUT, (request, response) -> logout(configuration, request, response));
        exception(NotAuthorizedException.class, (exception, request, response) -> handleNotAuthorizedException(configuration, exception, request, response));
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
	
	public void verify(Configuration configuration, Request request, Response response) throws JsonParseException, JsonMappingException, IOException {
		Optional<String> cookie = Optional.ofNullable(request.cookie(AUTH_TOKEN));
    	if (cookie.isPresent()) {
    		
    		Token token = objectMapper.readValue(cookie.get(), Token.class);
    		request.attribute(AUTH_TOKEN, token);
    		
    		Identity identity = new NowellpointClient(token)
    				.identity()
    				.get(token.getId());
    		
    		request.attribute("account", identity);
    		request.attribute("com.nowellpoint.default.locale", getDefaultLocale(configuration, identity));
    		request.attribute("com.nowellpoint.default.timezone", getDefaultTimeZone(configuration, identity));
    		
    	} else {
    		response.redirect(Path.Route.LOGIN.concat("?").concat(REDIRECT_URI).concat("=").concat(URLEncoder.encode(request.pathInfo(), "UTF-8")));
    		halt();
    	}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	
	public String showLoginPage(Configuration configuration, Request request, Response response) {
		Map<String, Object> model = getModel();
		model.put(REDIRECT_URI, request.queryParams(REDIRECT_URI));
        return render(configuration, request, response, model, Template.LOGIN);
    };
    
    /**
     * 
     * @param configuration
     * @param request
     * @param response
     * @return
     */
    
    public String login(Configuration configuration, Request request, Response response) {
    	
    	PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
    			.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
				.setUsername(request.queryParams("username"))
				.setPassword(request.queryParams("password"))
				.build();
		
		try {
			OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(passwordGrantRequest);

			Token token = oauthAuthenticationResponse.getToken();

	    	Long expiresIn = token.getExpiresIn();
	
	    	try {
	    		response.cookie(AUTH_TOKEN, new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true);
	    	} catch (IOException e) {
	    		throw new InternalServerErrorException(e);
	    	}

		} catch (OauthException e) {
			
			String acceptLanguages = request.headers("Accept-Language");
			
			LOGGER.info(acceptLanguages);
			
			String[] languages = acceptLanguages.trim().replace("-", "_").split(";");
			
			String[] locales = languages[0].split(",");
			
			LOGGER.info(locales[0]);
		
			if (e.getCode() == 7100) {
    			throw new NotAuthorizedException(MessageProvider.getMessage(Locale.US, "login.error"));
    		} else if (e.getCode() == 7101) {
    			throw new NotAuthorizedException(MessageProvider.getMessage(Locale.US, "disabled.account"));
    		} else {
    			LOGGER.error(e.getMessage());
    			throw new InternalServerErrorException(e.getMessage());
    		}
		}
		
		System.out.println(request.queryParams(REDIRECT_URI));
		
		if (request.queryParams(REDIRECT_URI) != null && ! request.queryParams(REDIRECT_URI).isEmpty()) {
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
	
    public String logout(Configuration configuration, Request request, Response response) {
		
		Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
    	
    	if (cookie.isPresent()) {
    		
    		Token token = null;
    		
    		try {
    			token = objectMapper.readValue(cookie.get(), Token.class);
    		} catch (IOException e) {
        		throw new InternalServerErrorException(e);
        	}
    		
    		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
					.setToken(token)
					.build();
			
			Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);

        	response.removeCookie(AUTH_TOKEN); 
    	}
    	
    	request.session().invalidate();
    	
    	response.redirect(Path.Route.INDEX);
    	
    	return "";
	};
	
	/**
	 * 
	 * @param configuration
	 * @param exception
	 * @param request
	 * @param response
	 */
	
	private void handleNotAuthorizedException(Configuration configuration, Exception exception, Request request, Response response) {		
		Map<String, Object> model = new HashMap<>();
    	model.put("errorMessage", exception.getMessage());
    	
    	String output = render(configuration, request, response, model, Template.LOGIN);
    	
    	response.status(401);
    	response.body(output);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param accountProfile
	 * @return
	 */
	
	private TimeZone getDefaultTimeZone(Configuration configuration, Identity identity) {		
		TimeZone timeZone = null;
		if (identity != null && identity.getTimeZoneSidKey() != null) {
			timeZone = TimeZone.getTimeZone(identity.getTimeZoneSidKey());
		} else {
			timeZone = TimeZone.getTimeZone(configuration.getTimeZone().getID());
		}
		
		return timeZone;
	}
	
	/**
	 * 
	 * @param configuration
	 * @param accountProfile
	 * @return
	 */
	
	private Locale getDefaultLocale(Configuration configuration, Identity identity) {
		Locale locale = null;
		if (identity != null && identity.getLocaleSidKey() != null) {
			String[] attrs = identity.getLocaleSidKey().split("_");
			if (attrs.length == 1) {
				locale = new Locale(attrs[0]);
			} else if (attrs.length == 2) {
				locale = new Locale(attrs[0], attrs[1]);
			} else if (attrs.length == 3) {
				locale = new Locale(attrs[0], attrs[1], attrs[3]);
			}
		} else {
			locale = configuration.getLocale();
		}
		return locale;
	}
}
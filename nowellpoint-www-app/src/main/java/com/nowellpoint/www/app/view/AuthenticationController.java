package com.nowellpoint.www.app.view;

import static spark.Spark.halt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.idp.Token;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ExceptionHandler;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;

public class AuthenticationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
	private static final String AUTH_TOKEN = "com.nowellpoint.auth.token";
	private static final String REDIRECT_URL = "com.nowellpoint.redirect.url";
	
	public AuthenticationController(Configuration configuration) {
		super(AuthenticationController.class, configuration);
	}
	
	/**
	 * 
	 */
	
	public Route showLoginPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        return render(request, model, Path.Template.LOGIN);
    };
    
    /**
     * 
     */
    
    public static Route login = (Request request, Response response) -> {
    	
    	PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
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
    			throw new NotAuthorizedException(MessageProvider.getMessage(Locale.US, "login.error"), Status.NOT_AUTHORIZED);
    		} else if (e.getCode() == 7101) {
    			throw new NotAuthorizedException(MessageProvider.getMessage(Locale.US, "disabled.account"), Status.NOT_AUTHORIZED);
    		} else {
    			LOGGER.error(e.getMessage());
    			throw new NotAuthorizedException(MessageProvider.getMessage(Locale.US, "login.error"), Status.NOT_AUTHORIZED);
    		}
		}
		
		if (request.cookie(REDIRECT_URL) != null) {
    		response.redirect(request.cookie(REDIRECT_URL));
    		response.removeCookie(REDIRECT_URL);
    	} else {
    		response.redirect(Path.Route.DASHBOARD);
    	}
    		
    	return "";
    };
    
    /**
     * 
     */
	
    public static Route logout = (Request request, Response response) -> {
		
		Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
    	
    	if (cookie.isPresent()) {
    		
    		Token token = null;
    		
    		try {
    			token = objectMapper.readValue(cookie.get(), Token.class);
    		} catch (IOException e) {
        		throw new InternalServerErrorException(e);
        	}
    		
    		try {
    			RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
    					.setAccessToken(token.getAccessToken())
    					.build();
    			
    			Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
    			
    		} catch (OauthException e) {
    			throw new BadRequestException(e.getMessage());
    		}
        	
	    	response.removeCookie(REDIRECT_URL);
        	response.removeCookie(AUTH_TOKEN); 
    	}
    	
    	request.session().invalidate();
    	
    	response.redirect(Path.Route.INDEX);
    	
    	return "";
	};
	
	
	/**
	 * 
	 */
	
	public static Filter verify = (Request request, Response response) -> {
    	Optional<String> cookie = Optional.ofNullable(request.cookie(AUTH_TOKEN));
    	if (cookie.isPresent()) {
    		Token token = objectMapper.readValue(cookie.get(), Token.class);
    		request.attribute(AUTH_TOKEN, token);
    		
    		AccountProfile accountProfile = new NowellpointClient(new TokenCredentials(token))
    				.accountProfile()
    				.getMyAccountProfile();
    		
    		request.attribute("account", accountProfile);
    		
    		//if (accountProfile.getSubscription() == null && ! Path.Route.PLANS.equals(request.pathInfo())) {
    		//	response.redirect(Path.Route.PLANS.replace(":id", accountProfile.getId());
    		//	halt();
    		//}
    		
    	} else {
    		response.cookie("/", REDIRECT_URL, request.pathInfo(), 72000, Boolean.TRUE);
    		response.redirect(Path.Route.LOGIN);
    		halt();
    	}
	};
	
	/**
	 * 
	 */
	
	public ExceptionHandler handleNotAuthorizedException = (Exception exception, Request request, Response response) -> {
		Map<String, Object> model = new HashMap<>();
    	model.put("errorMessage", exception.getMessage());
    	
    	String output = render(request, model, Path.Template.LOGIN);
    	
    	response.status(400);
    	response.body(output);
	};
}
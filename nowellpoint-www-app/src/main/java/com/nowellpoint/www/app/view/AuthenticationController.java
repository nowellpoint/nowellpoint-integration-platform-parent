package com.nowellpoint.www.app.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Route;

public class AuthenticationController extends AbstractController {
	
	private static final String REDIRECT_COOKIE = "com.nowellpoint.redirectUrl";
	
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
    
    public Route login = (Request request, Response response) -> {
    	PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setUsername(System.getenv("STORMPATH_USERNAME"))
				.setPassword(System.getenv("STORMPATH_PASSWORD"))
				.build();
		
		try {
			OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(passwordGrantRequest);
			
			Token token = oauthAuthenticationResponse.getToken();
    		
	    	Long expiresIn = token.getExpiresIn();
	    		
	    	try {
	    		response.cookie("com.nowellpoint.auth.token", new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true); 
	    	} catch (IOException e) {
	    		throw new InternalServerErrorException(e);
	    	}

		} catch (OauthException e) {
			if (e.getCode() == 7100) {
    			throw new NotAuthorizedException(MessageProvider.getMessage(getDefaultLocale(request), "login.error"), Status.NOT_AUTHORIZED);
    		} else if (e.getCode() == 7101) {
    			throw new NotAuthorizedException(MessageProvider.getMessage(getDefaultLocale(request), "disabled.account"), Status.NOT_AUTHORIZED);
    		} else {
    			throw new NotAuthorizedException(e.getMessage(), Status.NOT_AUTHORIZED);
    		}
		}
		
		if (request.cookie(REDIRECT_COOKIE) != null) {
    		response.redirect(request.cookie(REDIRECT_COOKIE));
    		response.removeCookie(REDIRECT_COOKIE);
    	} else {
    		response.redirect(Path.Route.START);
    	}
    		
    	return "";
    };
    
    /**
     * 
     */
	
    public Route logout = (Request request, Response response) -> {
		
		Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
    	
    	if (cookie.isPresent()) {
    		
    		Token token = null;
    		
    		try {
    			token = new ObjectMapper().readValue(cookie.get(), Token.class);
    		} catch (IOException e) {
        		throw new InternalServerErrorException(e);
        	}
    		
    		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
					.bearerAuthorization(token.getAccessToken())
	    			.path("oauth")
	    			.path("token")
	    			.execute();
	    	
	    	int statusCode = httpResponse.getStatusCode();
	    	
	    	if (statusCode != 204) {
	    		throw new BadRequestException(httpResponse.getAsString());
	    	}
        	
	    	response.removeCookie("com.nowellpoint.redirectUrl");
        	response.removeCookie("com.nowellpoint.oauth.token"); 
    	}
    	
    	request.session().invalidate();
    	
    	response.redirect(Path.Route.INDEX);
    	
    	return "";
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
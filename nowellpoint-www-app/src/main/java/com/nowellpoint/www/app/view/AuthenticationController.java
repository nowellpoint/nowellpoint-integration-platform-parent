package com.nowellpoint.www.app.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;

import freemarker.template.Configuration;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Route;

public class AuthenticationController extends AbstractController {
	
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
    	HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
    			.accept(MediaType.APPLICATION_JSON)
    			.path("oauth")
    			.path("token")
    			.basicAuthorization(request.queryParams("username"), request.queryParams("password"))
    			.execute();
    			
    	int statusCode = httpResponse.getStatusCode();
    			
    	if (statusCode != 200) {
    		ObjectNode error = httpResponse.getEntity(ObjectNode.class);
    		if (error.get("code").asInt() == 7100) {
    			throw new NotAuthorizedException(getValue(request, "login.error"), Status.NOT_AUTHORIZED);
    		} else if (error.get("code").asInt() == 7101) {
    			throw new NotAuthorizedException(getValue(request, "disabled.account"), Status.NOT_AUTHORIZED);
    		} else {
    			throw new NotAuthorizedException(error.get("message").asText(), Status.NOT_AUTHORIZED);
    		}
    	}
    			
    	Token token = httpResponse.getEntity(Token.class);
    		
    	Long expiresIn = token.getExpiresIn();
    		
    	try {
    		response.cookie("com.nowellpoint.auth.token", new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true); 
    	} catch (IOException e) {
    		throw new InternalServerErrorException(e);
    	}
    	
    	if (request.cookie("com.nowellpoint.redirectUrl") != null) {
    		response.redirect(request.cookie("com.nowellpoint.redirectUrl"));
    		response.removeCookie("com.nowellpoint.redirectUrl");
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
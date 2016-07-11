package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class AuthenticationController extends AbstractController {
	
	public AuthenticationController(Configuration cfg) {
		super(AuthenticationController.class, cfg);
	}
	
	@Override
	public void configureRoutes(Configuration cfg) {
		post("/login", (request, response) -> login(request, response));
        
        get("/logout", (request, response) -> logout(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String login(Request request, Response response) {
    	
    	HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
    			.accept(MediaType.APPLICATION_JSON)
    			.header("x-api-key", API_KEY)
    			.path("oauth")
    			.path("token")
    			.basicAuthorization(request.queryParams("username"), request.queryParams("password"))
    			.execute();
    			
    	int statusCode = httpResponse.getStatusCode();
    			
    	if (statusCode != 200) {
    		throw new NotAuthorizedException(getValue("login.error"), Status.NOT_AUTHORIZED);
    	}
    			
    	Token token = httpResponse.getEntity(Token.class);
    		
    	Long expiresIn = token.getExpiresIn();
    		
    	try {
    		response.cookie("com.nowellpoint.auth.token", new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true); 
    	} catch (IOException e) {
    		throw new InternalServerErrorException(e);
    	}
    		
    	response.redirect("/app/start");
    		
    	return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String logout(Request request, Response response) {
		
		Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
    	
    	if (cookie.isPresent()) {
    		
    		Token token = null;
    		
    		try {
    			token = new ObjectMapper().readValue(cookie.get(), Token.class);
    		} catch (IOException e) {
        		throw new InternalServerErrorException(e);
        	}
    		
    		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
					.header("x-api-key", API_KEY)
					.bearerAuthorization(token.getAccessToken())
	    			.path("oauth")
	    			.path("token")
	    			.execute();
	    	
	    	int statusCode = httpResponse.getStatusCode();
	    	
	    	if (statusCode != 204) {
	    		throw new BadRequestException(httpResponse.getAsString());
	    	}
        	
        	response.removeCookie("com.nowellpoint.oauth.token"); 
    	}
    	
    	request.session().invalidate();
    	
    	response.redirect("/");
    	
    	return "";
	}
}
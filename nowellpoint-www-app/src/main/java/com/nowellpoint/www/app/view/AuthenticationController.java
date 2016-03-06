package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class AuthenticationController {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
	private static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	private static final String API_KEY = System.getenv("NCS_API_KEY");
	
	public AuthenticationController(Configuration cfg) {
		
		get("/login", (request, response) -> getLogin(request, response), new FreeMarkerEngine(cfg));
        
        post("/login", (request, response) -> postLogin(request, response));
        
        get("/logout", (request, response) -> getLogout(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getLogin(Request request, Response response) {
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView(model, "login.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String postLogin(Request request, Response response) throws IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
    	
    	HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
    			.accept(MediaType.APPLICATION_JSON)
    			.header("x-api-key", API_KEY)
    			.path("oauth")
    			.path("token")
    			.basicAuthorization(request.queryParams("username"), request.queryParams("password"))
    			.execute();
    			
    	int statusCode = httpResponse.getStatusCode();
    			
    	LOGGER.info("Authenticate Status Code: " + statusCode + " Method: POST : " + httpResponse.getURL());
    			
    	if (statusCode != 200) {
    		throw new NotAuthorizedException(MessageProvider.getMessage(Locale.US, "loginError"), Status.UNAUTHORIZED);
    	}
    			
    	Token token = httpResponse.getEntity(Token.class);
    		
    	Long expiresIn = token.getExpiresIn();
    		
    	response.cookie("com.nowellpoint.auth.token", objectMapper.writeValueAsString(token), expiresIn.intValue(), true); 
    		
    	response.redirect("/app/start");
    		
    	return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String getLogout(Request request, Response response) throws IOException {
		
		Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
    	
    	if (cookie.isPresent()) {
    		Token token = new ObjectMapper().readValue(cookie.get(), Token.class);
    		
    		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
					.header("x-api-key", API_KEY)
					.bearerAuthorization(token.getAccessToken())
	    			.path("oauth")
	    			.path("token")
	    			.execute();
	    	
	    	int statusCode = httpResponse.getStatusCode();
	    	
	    	LOGGER.info("Status Code: " + statusCode + " Method: DELETE : " + httpResponse.getURL());
	    	
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
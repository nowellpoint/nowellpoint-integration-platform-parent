package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.client.AuthenticationException;
import com.nowellpoint.www.app.client.IdentityProviderException;
import com.nowellpoint.www.app.client.NCSAuthClient;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class AuthenticationController {
	
	private static NCSAuthClient ncsAuthClient = new NCSAuthClient();
	
	public AuthenticationController(Configuration cfg) {
		
        //
        // GET
        //
        
		get("/login", (request, response) -> {
			Map<String, Object> model = new HashMap<String, Object>();
			return new ModelAndView(model, "login.html");
		}, new FreeMarkerEngine(cfg));
        
        //
        // POST
        //
        
        post("/login", (request, response) -> {
        	
        	try {
        		
        		ObjectMapper objectMapper = new ObjectMapper();
        		
        		Token token = ncsAuthClient.authenticate(request.queryParams("username"), request.queryParams("password"));
        		
        		Long expiresIn = token.getExpiresIn();
        		
        		response.cookie("com.nowellpoint.auth.token", objectMapper.writeValueAsString(token), expiresIn.intValue(), true); 
        		
        		response.redirect("/app/dashboard");
        		
        		return "";
        		
        	} catch (AuthenticationException e) {
        		
        		Map<String, Object> model = new HashMap<String, Object>();
        		model.put("errorMessage", MessageProvider.getMessage(Locale.US, "loginError"));
        		
        		return new ModelAndView(model, "login.html");
        		
        	} catch (IdentityProviderException e) {
        		throw new InternalServerErrorException(e);
        	}
        	
        });
        
        //
        // GET
        // 
        
        get("/logout", (request, response) -> {
        	
        	Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
        	
        	if (cookie.isPresent()) {
        		Token token = new ObjectMapper().readValue(cookie.get(), Token.class);
        		
        		ncsAuthClient.logout(token.getAccessToken());
            	
            	response.removeCookie("com.nowellpoint.oauth.token"); 
        	}
        	
        	request.session().invalidate();
        	
        	response.redirect("/");
        	
        	return "";
        	
        });
	}
}
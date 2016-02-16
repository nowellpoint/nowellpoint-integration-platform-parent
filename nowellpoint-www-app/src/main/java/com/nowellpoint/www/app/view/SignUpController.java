package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class SignUpController {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
	
	public SignUpController(Configuration cfg) {
		
        //
        // GET
        //
        
        get("/signup", (request, response) -> {
			Map<String, Object> attributes = new HashMap<String, Object>();
        	return new ModelAndView(attributes, "signup.html");       	
        }, new FreeMarkerEngine(cfg));
        
        //
        // POST
        //
        
        post("/signup", (request, response) -> {
        	
        	Map<String, Object> model = new HashMap<String, Object>();
        	
        	if (request.queryParams("password").equals(request.queryParams("confirmPassword"))) {
        		      		
        		StringBuilder body = new StringBuilder();
    			request.queryParams().stream().limit(1).forEach(param -> {
    				body.append(param).append("=").append(request.queryParams(param));
    			});
    			request.queryParams().stream().skip(1).forEach(param -> {
    				body.append("&").append(param).append("=").append(request.queryParams(param));
            	});
	        	
	    		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
	        			.header("x-api-key", System.getenv("NCS_API_KEY"))
	        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	        			.acceptCharset("UTF-8")
	        			.path("signup")
	        			.body(body.toString())
	        			.execute();
	        	
	        	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
	        		        	
	        	if (httpResponse.getStatusCode() == 200) {
	        		model.put("successMessage", MessageProvider.getMessage(Locale.US, "signUpConfirm"));   
	        	} else {
	        		JsonNode error = httpResponse.getEntity(JsonNode.class);
	        		model.put("errorMessage", error.get("message").asText());
	        	}
	        	
        	} else {
        		model.put("errorMessage", MessageProvider.getMessage(Locale.US, "passwordMismatch"));
        	}
        	
        	return new ModelAndView(model, "signup.html");
        	
        }, new FreeMarkerEngine(cfg));
	}
}
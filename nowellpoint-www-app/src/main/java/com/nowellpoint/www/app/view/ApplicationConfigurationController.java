package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.www.app.model.sforce.Organization;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class ApplicationConfigurationController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationConfigurationController.class);
	
	public ApplicationConfigurationController(Configuration cfg) {
		
		//
		//
		//
		
		get("/app/applications/configure/salesforce", (request, response) -> {
			
			Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.salesforce.token"));
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("account", request.attribute("account"));
			model.put("organization", new Organization());
			
			if (cookie.isPresent()) {
				
				LOGGER.info("cookie not null");
				
				com.nowellpoint.aws.model.sforce.Token token = new ObjectMapper().readValue(Base64.getDecoder().decode(cookie.get()), com.nowellpoint.aws.model.sforce.Token.class);
				
				HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
	    				.header("Content-Type", "application/x-www-form-urlencoded")
	    				.header("x-api-key", System.getenv("NCS_API_KEY"))
	    				.bearerAuthorization(token.getAccessToken())
	        			.path("salesforce")
	        			.path("organization")
	        			.queryParameter("id", token.getId())
	        			.execute();
				
				LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
				
				Organization organization = httpResponse.getEntity(Organization.class);
				
				model.put("organization", organization);
			
			}

			return new ModelAndView(model, "secure/salesforce.html");			
			
		}, new FreeMarkerEngine(cfg));
	}

}

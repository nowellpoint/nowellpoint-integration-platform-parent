package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Base64;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;

import freemarker.log.Logger;
import freemarker.template.Configuration;

public class SalesforceController {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceController.class.getName());
	
	public SalesforceController(Configuration cfg) {
		
		get("/app/salesforce/oauth", (request, response) -> {
			
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
        			.header("x-api-key", System.getenv("NCS_API_KEY"))
        			.path("salesforce")
        			.path("oauth")
        			.execute();
        	
        	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " : " + httpResponse.getHeaders().get("Location"));
			
			response.redirect(httpResponse.getHeaders().get("Location").get(0));		
			
			return null;
		});
		
        //
        // GET
        //
        
        get("/app/callback", (request, response) -> {
        	
        	Optional<String> code = Optional.ofNullable(request.queryParams("code"));
        	
        	if (! code.isPresent()) {
        		throw new BadRequestException("missing OAuth code from Salesforce");
        	}
        	
        	HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
    				.header("Content-Type", "application/x-www-form-urlencoded")
    				.header("x-api-key", System.getenv("NCS_API_KEY"))
        			.path("salesforce")
        			.path("token")
        			.queryParameter("code", request.queryParams("code"))
        			.execute();
        	
        	int statusCode = httpResponse.getStatusCode();
        	
        	LOGGER.info("Status Code: " + statusCode + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
        	
        	if (statusCode != 200) {
        		throw new BadRequestException(httpResponse.getEntity());
        	}
        	
        	response.cookie("com.nowellpoint.auth.salesforce.token", Base64.getEncoder().encodeToString(httpResponse.getEntity().getBytes()), 300, true); 
        	
        	response.redirect("/app/applications/configure/salesforce");
        	
        	return "";
        	
        });
	}
}
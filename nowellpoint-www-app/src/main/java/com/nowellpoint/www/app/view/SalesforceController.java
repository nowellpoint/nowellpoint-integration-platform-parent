package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;

import freemarker.log.Logger;
import freemarker.template.Configuration;

public class SalesforceController {
	
	private static final Logger logger = Logger.getLogger(SalesforceController.class.getName());
	
	public SalesforceController(Configuration cfg) {
		
		get("/app/salesforce/oauth", (request, response) -> {
			
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
        			.header("x-api-key", System.getenv("NCS_API_KEY"))
        			.path("salesforce")
        			.path("oauth")
        			.execute();
        	
        	logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " : " + httpResponse.getHeaders().get("Location"));
			
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
        	
        	response.redirect("/app/applications/setup/salesforce?code=".concat(code.get()));
        	
        	return "";
        	
        });
	}
}
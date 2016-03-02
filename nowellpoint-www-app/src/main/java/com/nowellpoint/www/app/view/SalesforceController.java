package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.Token;
import com.nowellpoint.www.app.model.sforce.Organization;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceController {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceController.class.getName());
	
	public SalesforceController(Configuration cfg) {
		
		get("/app/salesforce/oauth", (request, response) -> oauth(request, response));
        
        get("/app/callback", (request, response) -> callback(request, response));
        
        get("/app/applications/configure/salesforce", (request, response) -> configureSalesforce(request, response), new FreeMarkerEngine(cfg));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String oauth(Request request, Response response) throws IOException {
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
    			.header("x-api-key", System.getenv("NCS_API_KEY"))
    			.path("salesforce")
    			.path("oauth")
    			.execute();
    	
    	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " : " + httpResponse.getHeaders().get("Location"));
		
		response.redirect(httpResponse.getHeaders().get("Location").get(0));		
		
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String callback(Request request, Response response) throws IOException {
    	
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
    		throw new BadRequestException(httpResponse.getAsString());
    	}
    	
    	response.cookie("com.nowellpoint.auth.salesforce.token", Base64.getEncoder().encodeToString(httpResponse.getAsString().getBytes()), 300, true); 
    	
    	response.redirect("/app/applications/configure/salesforce");
    	
    	return "";
    	
    }
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView configureSalesforce(Request request, Response response) throws IOException {
		
		Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.salesforce.token"));
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("organization", new Organization());
		
		if (cookie.isPresent()) {
			
			Token token = new ObjectMapper().readValue(Base64.getDecoder().decode(cookie.get()), Token.class);
			
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
	}
}
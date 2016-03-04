package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.www.app.model.sforce.UserInfo;

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
        
        get("/app/callback", (request, response) -> callback(request, response), new FreeMarkerEngine(cfg));
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
	
	private static ModelAndView callback(Request request, Response response) throws IOException {
    	
    	Optional<String> code = Optional.ofNullable(request.queryParams("code"));
    	
    	if (! code.isPresent()) {
    		throw new BadRequestException("missing OAuth code from Salesforce");
    	}
    	
    	HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
    			.path("salesforce")
    			.path("user-info")
    			.queryParameter("code", request.queryParams("code"))
    			.execute();
    	
    	int statusCode = httpResponse.getStatusCode();
    	
    	LOGGER.info("Status Code: " + statusCode + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
    	
    	if (statusCode != 200) {
    		throw new BadRequestException(httpResponse.getAsString());
    	}
    	
    	UserInfo userInfo = httpResponse.getEntity(UserInfo.class);
    	
    	Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("userInfo", userInfo);
    	
		return new ModelAndView(model, "secure/salesforce.html");	
    }
}
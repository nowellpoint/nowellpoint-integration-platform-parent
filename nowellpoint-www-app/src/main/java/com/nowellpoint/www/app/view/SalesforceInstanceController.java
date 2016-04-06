package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.sforce.SalesforceInstance;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceInstanceController {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceInstanceController.class.getName());
	
	public SalesforceInstanceController(Configuration cfg) {
		
		get("/app/salesforce/oauth", (request, response) -> oauth(request, response));
        
        get("/app/salesforce/callback", (request, response) -> callback(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/instances", (request, response) -> getSalesforceInstances(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/instance", (request, response) -> getSalesforceInstance(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/instance", (request, response) -> saveSalesforceInstance(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/salesforce/instance", (request, response) -> deleteSalesforceInstance(request, response), new FreeMarkerEngine(cfg));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String oauth(Request request, Response response) {
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
    			.header("x-api-key", System.getenv("NCS_API_KEY"))
    			.path("salesforce")
    			.path("oauth")
    			.queryParameter("state", request.queryParams("id"))
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
	
	private static ModelAndView callback(Request request, Response response) {
    	
    	Optional<String> code = Optional.ofNullable(request.queryParams("code"));
    	
    	if (! code.isPresent()) {
    		throw new BadRequestException("missing OAuth code from Salesforce");
    	}
    	
		return new ModelAndView(new HashMap<String, Object>(), "secure/salesforce-callback.html");	
    }
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSalesforceInstance(Request request, Response response) {
		
		Token token = request.attribute("token");
    	
    	HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("instance")
    			.queryParameter("code", request.queryParams("code"))
    			.execute();
    	
    	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
    	
    	SalesforceInstance salesforceInstance = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		salesforceInstance = httpResponse.getEntity(SalesforceInstance.class);	
    	} else {
    		throw new BadRequestException(httpResponse.getAsString());
    	}	
    	
    	Account account = request.attribute("account");
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("salesforceInstance", salesforceInstance);	
    	
    	return new ModelAndView(model, "secure/salesforce-authenticate.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSalesforceInstances(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("instances")
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<SalesforceInstance> salesforceInstances = httpResponse.getEntityList(SalesforceInstance.class);
		
		Account account = request.attribute("account");
		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("salesforceInstanceList", salesforceInstances);
    	
    	return new ModelAndView(model, "secure/salesforce-instance-list.html");
    	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView saveSalesforceInstance(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
    			.path("instance")
    			.parameter("id", request.queryParams("id"))
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		SalesforceInstance salesforceInstance = null;
    	
    	if (httpResponse.getStatusCode() == Status.CREATED) {
    		salesforceInstance = httpResponse.getEntity(SalesforceInstance.class);	
    	} else {
    		throw new BadRequestException(httpResponse.getAsString());
    	}	
    	
    	Account account = request.attribute("account");
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("salesforceInstance", salesforceInstance);	
    	model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
    	
    	return new ModelAndView(model, "secure/salesforce-authenticate.html");
	}
	
	private static ModelAndView deleteSalesforceInstance(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
    			.path("instance")
    			.path(request.queryParams("id"))
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		return null;
		
	}
}
package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Identity;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class DashboardController {
	
	private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
	
	public DashboardController(Configuration cfg) {     
        get("/app/start", (request, response) -> routeToStart(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/dashboard", (request, response) -> routeToDashboard(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/providers", (request, response) -> routeToServiceProviders(request, response), new FreeMarkerEngine(cfg));
	}
	
	private static ModelAndView routeToStart(Request request, Response response) {
    	Map<String,Object> model = new HashMap<String,Object>();
    	model.put("account", request.attribute("account"));
		return new ModelAndView(model, "secure/start.html");
	}
	
	private static ModelAndView routeToDashboard(Request request, Response response) {
    	Map<String,Object> model = new HashMap<String,Object>();
    	model.put("account", request.attribute("account"));
		return new ModelAndView(model, "secure/dashboard.html");
	}
	
	private static ModelAndView routeToServiceProviders(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("user-profile")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		Identity identity = httpResponse.getEntity(Identity.class);
		
		LOGGER.info(new ObjectMapper().writeValueAsString(identity));
			
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("identity", identity);
    	
		return new ModelAndView(model, "secure/service-providers.html");
	}
}
package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;

import java.util.List;
import java.util.Map;

import javax.ws.rs.NotAuthorizedException;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Property;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class AdministrationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AdministrationController.class);
	
	public AdministrationController(Configuration cfg) {
		super(AdministrationController.class, cfg);
	}
	
	public void setupRoutes(Configuration cfg) {
		get("/app/administration", (request, response) -> getAdministrationHome(request, response), new FreeMarkerEngine(cfg));	
		
		get("/app/administration/cache", (request, response) -> getCache(request, response), new FreeMarkerEngine(cfg));	
		
		get("/app/administration/properties", (request, response) -> getProperties(request, response), new FreeMarkerEngine(cfg));	
		
		delete("/app/administration/cache", (request, response) -> purgeCache(request, response), new FreeMarkerEngine(cfg));	
	}
	
	private ModelAndView getAdministrationHome(Request request, Response response) {
		
		Account account = getAccount(request);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		
		return new ModelAndView(model, "secure/administration-home.html");
		
	}
	
	private ModelAndView getCache(Request request, Response response) {
		
		Account account = getAccount(request);
		
		
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		
		return new ModelAndView(model, "secure/cache.html");
		
	}
	
	private ModelAndView purgeCache(Request request, Response response) {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("cache")
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Account account = getAccount(request);
		Map<String, Object> model = getModel();
		model.put("account", account);
		
		return new ModelAndView(model, "secure/cache.html");
		
	}
	
	private ModelAndView getProperties(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("properties")
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() == Status.NOT_AUTHORIZED) {
			throw new NotAuthorizedException(httpResponse.getAsString());
		}
		
		List<Property> properties = httpResponse.getEntityList(Property.class);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("propertyList", properties);
		
		return new ModelAndView(model, "secure/properties-list.html");
	}
}
package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Application;
import com.nowellpoint.www.app.model.ServiceProvider;
import com.nowellpoint.www.app.model.sforce.SalesforceConnector;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ApplicationController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationController.class);
	
	public ApplicationController(Configuration cfg) {
		
		get("/app/application/provider/:id", (request, response) -> newApplication(request, response), new FreeMarkerEngine(cfg));
		
		get("/app/application/:id", (request, response) -> getApplication(request, response), new FreeMarkerEngine(cfg));
		
		get("/app/applications", (request, response) -> getApplications(request, response), new FreeMarkerEngine(cfg));
		
		delete("/app/application/:id", (request, response) -> deleteApplication(request, response));
		
		post("/app/application", (request, response) -> saveApplication(request, response), new FreeMarkerEngine(cfg));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView newApplication(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		String serviceProviderId = request.params(":id");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.path(serviceProviderId)
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		ServiceProvider provider = httpResponse.getEntity(ServiceProvider.class);
		
		httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("connectors")
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<SalesforceConnector> salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		
		Account account = request.attribute("account");
		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("serviceProvider", provider);
    	model.put("salesforceConnectorsList", salesforceConnectors);
		model.put("application", new Application());
		
		return new ModelAndView(model, "secure/application.html");
	}
	
	private static ModelAndView getApplication(Request request, Response response) {
		
		String applicationId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("application")
				.path(applicationId)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("application", new Application());
		
		return new ModelAndView(model, "secure/application.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView getApplications(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("application")
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<Application> applications = httpResponse.getEntityList(Application.class);
		
		applications = applications.stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toList());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("applicationList", applications);
		
		return new ModelAndView(model, "secure/application-list.html");
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView saveApplication(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		String body;
		try {
			body = new StringBuilder()
					.append("serviceProviderId=")
					.append(request.queryParams("serviceProviderId"))
					.append("&name=")
					.append(URLEncoder.encode(request.queryParams("name"), "UTF-8"))
					.append("&connectorId=")
					.append(request.queryParams("connectorId"))
					.toString();
			
		} catch (UnsupportedEncodingException e) {
			throw new BadRequestException(e);
		}
		
		HttpResponse httpResponse = null;
		
		if (request.queryParams("id").trim().isEmpty()) {
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("application")
					.body(body)
					.execute();
			
		} else {
			
			httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("application")
					.path(request.queryParams("id"))
					.body(body)
					.execute();
			
		}
		
		if (httpResponse.getStatusCode() != Status.OK && httpResponse.getStatusCode() != Status.CREATED) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Application application = httpResponse.getEntity(Application.class);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("application", application);
		
		return new ModelAndView(model, "secure/" + application.getServiceInstance().getConfigurationPage());
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String deleteApplication(Request request, Response response) {
		
		String applicationId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("application")
				.path(applicationId)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		return "";	
	}
}
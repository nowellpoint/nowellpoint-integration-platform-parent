package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Application;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ApplicationController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationController.class);
	
	public ApplicationController(Configuration cfg) {
		
		get("/app/applications", (request, response) -> getApplications(request, response), new FreeMarkerEngine(cfg));
		
		delete("/app/applications/:id", (request, response) -> deleteApplication(request, response));
		
		post("/app/applications/configure/salesforce", (request, response) -> saveSalesforceApplication(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView getApplications(Request request, Response response) throws IOException {
		
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
	 * @throws IOException
	 */
	
	private static String deleteApplication(Request request, Response response) throws IOException {
		
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
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String saveSalesforceApplication(Request request, Response response) throws IOException {
		Token token = request.attribute("token");
		Account account = request.attribute("account");
		
		Application application = new Application();
		application.setType(request.queryParams("type"));
		application.setIsSandbox(Boolean.valueOf(request.queryParams("isSandbox")));
		application.setName(request.queryParams("organizationName"));
		application.setKey(request.queryParams("organizationId"));
		application.setUrl(request.queryParams("url"));
		application.setInstanceName(request.queryParams("instanceName"));
		
		HttpResponse httpResponse;
		
		if (request.queryParams("id").trim().isEmpty()) {
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("application")
					.body(application)
					.execute();
			
		} else {
			
			httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("application")
					.body(application)
					.execute();
		}
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode() && httpResponse.getStatusCode() != Status.CREATED.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		application = httpResponse.getEntity(Application.class);
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("application", application);
		
		response.redirect("/app/applications");
		
		return "";
	}
}
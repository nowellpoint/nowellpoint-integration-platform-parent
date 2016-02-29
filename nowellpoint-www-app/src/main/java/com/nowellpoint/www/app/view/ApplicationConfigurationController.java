package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Application;
import com.nowellpoint.www.app.model.sforce.Organization;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ApplicationConfigurationController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationConfigurationController.class);
	
	public ApplicationConfigurationController(Configuration cfg) {
		
		get("/app/applications/configure/salesforce", (request, response) -> getSalesforce(request, response), new FreeMarkerEngine(cfg));
		
		post("/app/applications/configure/salesforce", (request, response) -> saveSalesforce(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView getSalesforce(Request request, Response response) throws IOException {
		
		Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.salesforce.token"));
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("organization", new Organization());
		
		if (cookie.isPresent()) {
			
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
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String saveSalesforce(Request request, Response response) throws IOException {
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
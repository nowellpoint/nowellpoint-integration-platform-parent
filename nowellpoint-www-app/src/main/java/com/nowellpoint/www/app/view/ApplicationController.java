package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.aws.model.sforce.Organization;
import com.nowellpoint.www.app.model.Application;
import com.nowellpoint.www.app.model.Identity;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class ApplicationController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationController.class);
	
	public ApplicationController(Configuration cfg) {
		
		//
	    // GET
	    //
	    
		get("/app/applications", (request, response) -> {
			
			Token token = request.attribute("token");
			
			LOGGER.info(token.getAccessToken());
			
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.path("identity")
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
			if (httpResponse.getStatusCode() != Response.Status.OK.getStatusCode()) {
				throw new NotFoundException(httpResponse.getEntity());
			}
			
			Identity identity = httpResponse.getEntity(Identity.class);
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("account", request.attribute("account"));
			model.put("applicationList", identity.getApplications());
			
			return new ModelAndView(model, "secure/application-list.html");
			
		}, new FreeMarkerEngine(cfg));
		
		get("/app/applications/:type", (request, response) -> {
			
			String type = request.params(":type");
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("account", request.attribute("account"));
			
			return new ModelAndView(model, String.format("secure/%s.html", type));
			
		}, new FreeMarkerEngine(cfg));
		
		get("/app/applications/setup/salesforce", (request, response) -> {
			
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
        	
        	//com.nowellpoint.aws.model.sforce.Token token = httpResponse.getEntity(com.nowellpoint.aws.model.sforce.Token.class);
        	ObjectNode token = httpResponse.getEntity(ObjectNode.class);
        	
        	response.cookie("com.nowellpoint.auth.salesforce.token", Base64.getEncoder().encodeToString(token.toString().getBytes()), 300, true); 
        	
    		httpResponse = RestResource.get(token.get("id").asText())
    				.acceptCharset(StandardCharsets.UTF_8)
    				.bearerAuthorization(token.get("access_token").asText())
    				.accept(MediaType.APPLICATION_JSON)
    				.queryParameter("version", "latest")
    				.execute();
        	
        	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
        	
        	com.nowellpoint.aws.model.sforce.Identity identity = httpResponse.getEntity(com.nowellpoint.aws.model.sforce.Identity.class);
        	
        	final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
         			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
         			+ "UsesStartDateAsFiscalYearName";
         	
    		httpResponse = RestResource.get(identity.getUrls().getSobjects())
         			.bearerAuthorization(token.get("access_token").asText())
         			.path("Organization")
         			.path(identity.getOrganizationId())
         			.queryParameter("fields", ORGANIZATION_FIELDS)
         			.queryParameter("version", "latest")
         			.execute();
         	
    		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
         	
         	Organization organization = httpResponse.getEntity(Organization.class);
        	
        	Map<String, Object> model = new HashMap<String, Object>();
			model.put("account", request.attribute("account"));
			model.put("identity", identity);
			model.put("organization", organization);
        	
			return new ModelAndView(model, "secure/salesforce.html");			
			
		}, new FreeMarkerEngine(cfg));
		
		post("/app/applications", (request, response) -> {
			
			Token token = request.attribute("token");
			
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.path("identity")
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
			if (httpResponse.getStatusCode() != Response.Status.OK.getStatusCode()) {
				throw new NotFoundException(httpResponse.getEntity());
			}
			
			Identity identity = httpResponse.getEntity(Identity.class);
			
			Application application = new Application();
			application.setId(request.queryParams("id"));
			application.setType(request.queryParams("type"));
			
			LOGGER.info(application.getId());
			LOGGER.info(application.getType());
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("identity")
					.path(identity.getId())
					.path("application")
					.body(application)
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
			response.redirect("/app/applications");
			
			return "";
		});
	}
}
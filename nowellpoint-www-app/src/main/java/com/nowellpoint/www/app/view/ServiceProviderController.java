package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.delete;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Identity;
import com.nowellpoint.www.app.model.ServiceProvider;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ServiceProviderController {
	
	private static final Logger LOGGER = Logger.getLogger(ServiceProviderController.class.getName());
	
	public ServiceProviderController(Configuration cfg) {     
        get("/app/providers", (request, response) -> routeToServiceProviders(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/providers", (request, response) -> saveServiceProvider(request, response));
        
        get("/app/providers/configure/:providerId", (request, response) -> saveServiceProvider(request, response));
        
        delete("/app/providers/:identityId", (request, response) -> removeServiceProvider(request, response));
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
			
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("identity", identity);
    	
		return new ModelAndView(model, "secure/service-providers.html");
	}
	
	private static ModelAndView routeToServiceProviderConfigure(Request request, Response response) {
		String providerId = request.params("identityId");
		
		Token token = request.attribute("token");
		
		return null;
		
	}
	
	private static String saveServiceProvider(Request request, Response response) throws IOException {
		
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
		
		ServiceProvider serviceProvider = new ServiceProvider();
		serviceProvider.setType(request.queryParams("type"));
		serviceProvider.setKey(request.queryParams("organizationId"));
		serviceProvider.setOwner(request.queryParams("organizationName"));
		serviceProvider.setAccount(request.queryParams("username"));
		serviceProvider.setIsActive(Boolean.TRUE);
		serviceProvider.setInstanceName(request.queryParams("instanceName"));
		serviceProvider.setInstanceUrl(request.queryParams("instanceUrl"));
		serviceProvider.setPrice(0.00);
		
		httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON)
				.path("identity")
				.path(identity.getId())
				.path("service-provider")
				.body(serviceProvider)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		response.redirect("/app/providers");
		
		return "";
	}
	
	private static String removeServiceProvider(Request request, Response response) throws IOException {
		String id = request.queryParams("id"); 
		String identityId = request.params("identityId");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON)
				.path("identity")
				.path(identityId)
				.path("service-provider")
				.path(id)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		return "";
	}
}
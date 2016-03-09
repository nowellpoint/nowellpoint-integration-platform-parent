package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
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
        get("/app/providers", (request, response) -> getServiceProviders(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/providers", (request, response) -> saveServiceProvider(request, response));
        
        get("/app/providers/:id", (request, response) -> getServiceProvider(request, response));
        
        delete("/app/providers/:id", (request, response) -> deleteServiceProvider(request, response));
	}
	
	private static ModelAndView getServiceProviders(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("provider")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		List<ServiceProvider> provider = httpResponse.getEntityList(ServiceProvider.class);
			
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("serviceProviders", provider);
    	
		return new ModelAndView(model, "secure/service-providers.html");
	}
	
	private static ModelAndView getServiceProvider(Request request, Response response) throws IOException {
		
		String serviceProviderId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("provider")
				.path(serviceProviderId)
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		return null;
		
	}
	
	private static String saveServiceProvider(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		Account account = request.attribute("account");
		
		Identity owner = new Identity();
		owner.setName(account.getFullName());
		owner.setHref(account.getHref());
		
		ServiceProvider serviceProvider = new ServiceProvider();
		serviceProvider.setType(request.queryParams("type"));
		serviceProvider.setKey(request.queryParams("organizationId"));
		serviceProvider.setOrganization(request.queryParams("organizationName"));
		serviceProvider.setAccount(request.queryParams("username"));
		serviceProvider.setIsActive(Boolean.TRUE);
		serviceProvider.setInstanceName(request.queryParams("instanceName"));
		serviceProvider.setInstanceUrl(request.queryParams("instanceUrl"));
		serviceProvider.setPrice(0.00);
		serviceProvider.setOwner(owner);
		
		HttpResponse httpResponse = null;
		
		if (request.queryParams("id") == null || request.queryParams("id").trim().isEmpty()) {
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("provider")
					.body(serviceProvider)
					.execute();
			
		} else {
			
			httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("provider")
					.body(serviceProvider)
					.execute();
			
		}
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		response.redirect("/app/providers");
		
		return "";
	}
	
	private static String deleteServiceProvider(Request request, Response response) throws IOException {
		String id = request.queryParams("id"); 
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("provider")
				.path(id)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		return "";
	}
}
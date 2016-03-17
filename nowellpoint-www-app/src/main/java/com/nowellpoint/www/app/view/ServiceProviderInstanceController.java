package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Identity;
import com.nowellpoint.www.app.model.SalesforceProfile;
import com.nowellpoint.www.app.model.ServiceProviderInstance;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ServiceProviderInstanceController {
	
	private static final Logger LOGGER = Logger.getLogger(ServiceProviderInstanceController.class.getName());
	
	public ServiceProviderInstanceController(Configuration cfg) {     
		
        get("/app/providers", (request, response) -> getServiceProviders(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/providers", (request, response) -> saveServiceProvider(request, response));
        
        get("/app/providers/:id", (request, response) -> getServiceProvider(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/providers/:id", (request, response) -> deleteServiceProvider(request, response));
	}
	
	private static ModelAndView getServiceProviders(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		List<ServiceProviderInstance> providers = httpResponse.getEntityList(ServiceProviderInstance.class);
		
		providers = providers.stream().sorted((p1, p2) -> p1.getCreatedDate().compareTo(p2.getCreatedDate())).collect(Collectors.toList());
			
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("serviceProviders", providers);
    	
		return new ModelAndView(model, "secure/service-providers.html");
	}
	
	private static ModelAndView getServiceProvider(Request request, Response response) throws IOException {
		
		String serviceProviderId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.path(serviceProviderId)
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		ServiceProviderInstance provider = httpResponse.getEntity(ServiceProviderInstance.class);
		
		httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
				.path(provider.getAccount())
				.path("describe")
				.execute();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("serviceProvider", provider);
		
		return new ModelAndView(model, "secure/service-provider-configure.html");
		
	}
	
	private static String saveServiceProvider(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		Account account = request.attribute("account");
		
		HttpResponse httpResponse = null;
		
		httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path("identity")
				.queryParameter("subject", account.getHref())
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode() && httpResponse.getStatusCode() != Status.CREATED.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Identity owner = httpResponse.getEntity(Identity.class);
		
		ServiceProviderInstance serviceProvider = new ServiceProviderInstance();
		serviceProvider.setType(request.queryParams("type"));
		serviceProvider.setKey(request.queryParams("organizationId"));
		serviceProvider.setOrganization(request.queryParams("organizationName"));
		serviceProvider.setAccount(request.queryParams("userId"));
		serviceProvider.setIsActive(Boolean.TRUE);
		serviceProvider.setInstanceName(request.queryParams("instanceName"));
		serviceProvider.setInstanceUrl(request.queryParams("instanceUrl"));
		serviceProvider.setPrice(0.00);
		serviceProvider.setOwner(owner);
		
		if (request.queryParams("id") == null || request.queryParams("id").trim().isEmpty()) {
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("providers")
					.body(serviceProvider)
					.execute();
			
		} else {
			
			httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("providers")
					.body(serviceProvider)
					.execute();
			
		}
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode() && httpResponse.getStatusCode() != Status.CREATED.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		SalesforceProfile salesforceProfile = new SalesforceProfile();
		salesforceProfile.setCity(request.queryParams("city"));
		salesforceProfile.setCountry(request.queryParams("country"));
		salesforceProfile.setDisplayName(request.queryParams("displayName"));
		salesforceProfile.setEmail(request.queryParams("email"));
		salesforceProfile.setFirstName(request.queryParams("firstName"));
		salesforceProfile.setUserId(request.queryParams("userId"));
		salesforceProfile.setLanguage(request.queryParams("language"));
		salesforceProfile.setLastName(request.queryParams("lastName"));
		salesforceProfile.setLocale(new Locale(request.queryParams("locale")));
		salesforceProfile.setMobilePhone(request.queryParams("mobilePhone"));
		salesforceProfile.setState(request.queryParams("state"));
		salesforceProfile.setStreet(request.queryParams("street"));
		salesforceProfile.setUsername(request.queryParams("username"));
		salesforceProfile.setZipPostalCode(request.queryParams("zipPostalCode"));
		salesforceProfile.getPhotos().setProfilePicture(request.queryParams("profilePicture"));
		
		httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.path("identity")
				.path(owner.getId())
				.path("salesforce-profile")
				.body(salesforceProfile)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode() && httpResponse.getStatusCode() != Status.CREATED.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		System.out.println(ServiceProviderInstanceController.class.getName() + " " + httpResponse.getAsString());
		
		response.redirect("/app/providers");
    	
		return "";
	}
	
	private static String deleteServiceProvider(Request request, Response response) throws IOException {
		
		String id = request.params("id"); 
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.path(id)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() != Status.NO_CONTENT.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		return "";
	}
}
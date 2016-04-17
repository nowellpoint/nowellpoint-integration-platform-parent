package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.SalesforceConnector;
import com.nowellpoint.www.app.model.ServiceInstance;
import com.nowellpoint.www.app.model.ServiceProvider;
import com.nowellpoint.www.app.model.sforce.Sobject;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceConnectorController {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceConnectorController.class.getName());
	
	public SalesforceConnectorController(Configuration cfg) {
		
		get("/app/salesforce/oauth", (request, response) -> oauth(request, response));
        
        get("/app/salesforce/callback", (request, response) -> callback(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connectors", (request, response) -> getSalesforceConnectors(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connector", (request, response) -> getSalesforceConnectorDetails(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/connector", (request, response) -> saveSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connector/:id", (request, response) -> getSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/salesforce/connector/:id", (request, response) -> deleteSalesforceConnector(request, response));
        
        get("/app/salesforce/connector/:id/providers", (request, response) -> getServiceProviders(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connector/:id/service/:key", (request, response) -> getService(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/connector/:id/service", (request, response) -> addService(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connector/:id/service/:key/sobjects", (request, response) -> getSobjects(request, response), new FreeMarkerEngine(cfg));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getService(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		Account account = request.attribute("account");
		
    	ServiceInstance serviceInstance = getServiceInstance(token.getAccessToken(), request.params(":id"), request.params(":key"));
    	
    	Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("serviceInstance", serviceInstance);
		model.put("sobjects", Collections.EMPTY_LIST);
		
		return new ModelAndView(model, "secure/".concat(serviceInstance.getConfigurationPage()));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView addService(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		Account account = request.attribute("account");
		
		String body = new StringBuilder()
					.append("serviceProviderId=")
					.append(request.queryParams("serviceProviderId"))
					.toString();
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("salesforce")
				.path("connector")
				.path(request.params(":id"))
				.path("service")
				.body(body)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		
		List<ServiceProvider> providers = getServiceProviders(token.getAccessToken());
		
		model.put("serviceProviders", providers);
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			SalesforceConnector salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
			
	    	model.put("salesforceConnector", salesforceConnector);
	    	model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			
		} else {
			model.put("errorMessage", httpResponse.getAsString());
		}
		
    	return new ModelAndView(model, "secure/service-catalog.html");
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
	
	private static ModelAndView getSalesforceConnectorDetails(Request request, Response response) {
		
		Token token = request.attribute("token");
    	
    	HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("connector")
    			.queryParameter("code", request.queryParams("code"))
    			.execute();
    	
    	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
    	
    	Account account = request.attribute("account");
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	
    	SalesforceConnector salesforceConnector = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);	
    		model.put("salesforceConnector", salesforceConnector);
        	model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
    	} else {
    		model.put("errorMessage", httpResponse.getAsString());
    	}	
    	
    	return new ModelAndView(model, "secure/salesforce-authenticate.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSalesforceConnector(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		Account account = request.attribute("account");
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
		
		SalesforceConnector salesforceConnector = null;
		
		try {
			salesforceConnector = getSalesforceConnector(token.getAccessToken(), request.params(":id"));
			model.put("salesforceConnector", salesforceConnector);
		} catch (BadRequestException e) {
			model.put("errorMessage", e.getMessage());
		}
		
		return new ModelAndView(model, "secure/salesforce-connector.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSalesforceConnectors(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("connectors")
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<SalesforceConnector> salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		
		salesforceConnectors.stream().sorted((p1, p2) -> p1.getCreatedDate().compareTo(p2.getCreatedDate())).collect(Collectors.toList());
		
		Account account = request.attribute("account");
		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("salesforceConnectorsList", salesforceConnectors);
    	
    	return new ModelAndView(model, "secure/salesforce-connectors-list.html");
    	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView saveSalesforceConnector(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
    			.path("connector")
    			.parameter("id", request.queryParams("id"))
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		SalesforceConnector salesforceConnector = null;
    	
    	if (httpResponse.getStatusCode() == Status.CREATED) {
    		salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);	
    	} else {
    		throw new BadRequestException(httpResponse.getAsString());
    	}	
    	
    	Account account = request.attribute("account");
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("salesforceConnector", salesforceConnector);	
    	model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
    	
    	return new ModelAndView(model, "secure/salesforce-authenticate.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String deleteSalesforceConnector(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
    			.path("connector")
    			.path(request.params(":id"))
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getServiceProviders(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
			
		List<ServiceProvider> providers = getServiceProviders(token.getAccessToken());
		
		model.put("serviceProviders", providers);
		
		SalesforceConnector salesforceConnector = null;
		
		try {
			salesforceConnector = getSalesforceConnector(token.getAccessToken(), request.params(":id"));
			model.put("salesforceConnector", salesforceConnector);
		} catch (BadRequestException e) {
			model.put("errorMessage", e.getMessage());
		}
    	
		return new ModelAndView(model, "secure/service-catalog.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSobjects(Request request, Response response) {
		
		Token token = request.attribute("token");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
    			.path("sobjects")
    			.queryParameter("instance", request.queryParams("instance"))
    			.queryParameter("username", request.queryParams("username"))
    			.queryParameter("password", request.queryParams("password"))
    			.queryParameter("securityToken", request.queryParams("securityToken"))
    			.execute();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			List<Sobject> sobjects = httpResponse.getEntityList(Sobject.class);
			model.put("sobjects", sobjects);
		} else {
			model.put("errorMessage", httpResponse.getEntity(JsonNode.class).get("message").asText());
			model.put("sobjects", Collections.EMPTY_LIST);
		}
		
		ServiceInstance serviceInstance = getServiceInstance(token.getAccessToken(), request.params(":id"), request.params(":key"));
		model.put("serviceInstance", serviceInstance);
		
		return new ModelAndView(model, String.format("secure/%s", serviceInstance.getConfigurationPage()));
		
	}
	
	/**
	 * 
	 * @param accessToken
	 * @param id
	 * @return
	 */
	
	private static SalesforceConnector getSalesforceConnector(String accessToken, String id) {
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(accessToken)
    			.path("salesforce")
    			.path("connector")
    			.path(id)
    			.execute();
    	
    	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
    	
    	SalesforceConnector salesforceConnector = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);	
    		return salesforceConnector;
    	} else {
    		throw new BadRequestException(httpResponse.getAsString());
    	}
	}
	
	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	
	private static List<ServiceProvider> getServiceProviders(String accessToken) {
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(accessToken)
				.path("providers")
				.queryParameter("localeSidKey", "en_US")
				.queryParameter("languageLocaleKey", "en_US")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		List<ServiceProvider> providers = httpResponse.getEntityList(ServiceProvider.class);
		
		providers = providers.stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toList());
		
		return providers;
	}
	
	private static ServiceInstance getServiceInstance(String accessToken, String id, String key) {
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(accessToken)
    			.path("salesforce")
    			.path("connector")
    			.path(id)
    			.path("service")
    			.path(key)
    			.execute();
    	
    	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
		
    	ServiceInstance serviceInstance = httpResponse.getEntity(ServiceInstance.class);	
    	
    	return serviceInstance;
	}
}
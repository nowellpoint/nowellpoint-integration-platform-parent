package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import static com.nowellpoint.www.app.util.HtmlWriter.error;
import static com.nowellpoint.www.app.util.HtmlWriter.success;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.nowellpoint.www.app.model.Environment;
import com.nowellpoint.www.app.model.SalesforceConnector;
import com.nowellpoint.www.app.model.ServiceInstance;
import com.nowellpoint.www.app.model.ServiceProvider;
import com.nowellpoint.www.app.model.sforce.LoginResult;
import com.nowellpoint.www.app.model.sforce.Sobject;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.RequestWrapper;
import com.nowellpoint.www.app.util.ResourceBundleUtil;

import freemarker.ext.beans.ResourceBundleModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceConnectorController {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceConnectorController.class.getName());
	
	private static ResourceBundleModel labels;
	
	public SalesforceConnectorController(Configuration cfg) {
		
		labels = ResourceBundleUtil.getResourceBundle(SalesforceConnectorController.class.getName(), cfg.getLocale());
		
		get("/app/salesforce/oauth", (request, response) -> oauth(request, response));
        
        get("/app/salesforce/callback", (request, response) -> callback(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connectors", (request, response) -> getSalesforceConnectors(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connector", (request, response) -> getSalesforceConnectorDetails(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/connector", (request, response) -> saveSalesforceConnector(request, response));
        
        get("/app/salesforce/connector/:id", (request, response) -> getSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/salesforce/connector/:id", (request, response) -> deleteSalesforceConnector(request, response));
        
        get("/app/salesforce/connector/:id/providers", (request, response) -> getServiceProviders(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connector/:id/service/:key", (request, response) -> getService(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/connector/:id/service", (request, response) -> addService(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/connector/:id/service/:key/sobjects", (request, response) -> getSobjects(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/connector/:id/service/:key/configuration", (request, response) -> saveConfiguration(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connector/:id/service/:key/environments", (request, response) -> getEnvironments(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/salesforce/connector/:id/service/:key/environments", (request, response) -> saveEnvironments(request, response));
        
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getEnvironments(Request request, Response response) {
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token.getAccessToken(), request.params(":id"));
    	
    	Optional<ServiceInstance> serviceInstance = salesforceConnector
    			.getServiceInstances()
    			.stream()
    			.filter(p -> p.getKey().equals(request.params(":key")))
    			.findFirst();
    	
    	List<Environment> environments = null;
    	
    	if (serviceInstance.isPresent()) {
    		environments = serviceInstance.get().getEnvironments().stream().sorted((p1, p2) -> p1.getIndex().compareTo(p2.getIndex())).collect(Collectors.toList());
    	}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("id", salesforceConnector.getId());
		model.put("key", serviceInstance.get().getKey());
		model.put("providerName", serviceInstance.get().getProviderName());
		model.put("environments", environments);
		
		return new ModelAndView(model, "secure/environments.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getService(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
    	
    	SalesforceConnector salesforceConnector = getSalesforceConnector(token.getAccessToken(), request.params(":id"));
    	
    	Optional<ServiceInstance> serviceInstance = salesforceConnector
    			.getServiceInstances()
    			.stream()
    			.filter(p -> p.getKey().equals(request.params(":key")))
    			.findFirst();
    	
    	Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance.isPresent() ? serviceInstance.get() : null);
		model.put("sobjects", Collections.emptyList());
		model.put("loginResult", null);
		
		return new ModelAndView(model, "secure/".concat(serviceInstance.get().getConfigurationPage()));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView addService(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
		
		SalesforceConnector salesforceConnector = null;
		String successMessage = null;
		String errorMessage = null;
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("salesforce")
				.path("connector")
				.path(request.params(":id"))
				.path("service")
				.parameter("serviceProviderId", request.queryParams("serviceProviderId"))
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		List<ServiceProvider> providers = getServiceProviders(token.getAccessToken());
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
			successMessage = MessageProvider.getMessage(Locale.US, "saveSuccess");
		} else {
			errorMessage = httpResponse.getAsString();
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("loginResult", null);
		model.put("serviceProviders", providers);
		model.put("salesforceConnector", salesforceConnector);
		model.put("successMessage", successMessage);
		model.put("errorMessage", errorMessage);
		
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
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("labels", labels);
    	
		return new ModelAndView(model, "secure/salesforce-callback.html");	
    }
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSalesforceConnectorDetails(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
    	
    	HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("connector")
    			.queryParameter("code", request.queryParams("code"))
    			.execute();
    	
    	SalesforceConnector salesforceConnector = null;
    	String successMessage = null;
    	String errorMessage = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);	
    		successMessage = MessageProvider.getMessage(Locale.US, "saveSuccess");
    	} else {
    		errorMessage = httpResponse.getAsString();
    	}	
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("labels", labels);
    	model.put("salesforceConnector", salesforceConnector);
    	model.put("successMessage", successMessage);
    	model.put("errorMessage", errorMessage);
    	
    	return new ModelAndView(model, "secure/salesforce-authenticate.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSalesforceConnector(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
    	
		SalesforceConnector salesforceConnector = null;
		String errorMessage = null;
		
		try {
			salesforceConnector = getSalesforceConnector(token.getAccessToken(), request.params(":id"));
		} catch (BadRequestException e) {
			errorMessage = e.getMessage();
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("labels", labels);
    	model.put("salesforceConnector", salesforceConnector);
    	model.put("errorMessage", errorMessage);
		
		return new ModelAndView(model, "secure/salesforce-connector.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSalesforceConnectors(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("connectors")
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<SalesforceConnector> salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		
		salesforceConnectors.stream().sorted((p1, p2) -> p1.getCreatedDate().compareTo(p2.getCreatedDate())).collect(Collectors.toList());
		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("labels", labels);
    	model.put("salesforceConnectorsList", salesforceConnectors);
    	
    	return new ModelAndView(model, "secure/salesforce-connectors-list.html");
    	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String saveSalesforceConnector(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
    			.path("connector")
    			.parameter("id", request.queryParams("id"))
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
//		SalesforceConnector salesforceConnector = null;
//    	
//    	if (httpResponse.getStatusCode() == Status.CREATED) {
//    		salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);	
//    	} else {
//    		throw new BadRequestException(httpResponse.getAsString());
//    	}	
    	
    	String html = null;
		
		if (httpResponse.getStatusCode() == Status.CREATED) {
			html = success(MessageProvider.getMessage(Locale.US, "saveSuccess"));
		} else {
			html = error(httpResponse.getEntity(JsonNode.class).get("message").asText());
		}
		
		return html;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String deleteSalesforceConnector(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
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
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
		
		List<ServiceProvider> providers = Collections.emptyList();
		SalesforceConnector salesforceConnector = null;
		String errorMessage = null;
		
		try {
			providers = getServiceProviders(token.getAccessToken());
		} catch (BadRequestException e) {
			errorMessage = e.getMessage();
		}
		
		try {
			salesforceConnector = getSalesforceConnector(token.getAccessToken(), request.params(":id"));
		} catch (BadRequestException e) {
			errorMessage = e.getMessage();
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("serviceProviders", providers);
		model.put("salesforceConnector", salesforceConnector);
		model.put("errorMessage", errorMessage);
    	
		return new ModelAndView(model, "secure/service-catalog.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getSobjects(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		List<Sobject> sobjects = Collections.emptyList();
		LoginResult result = null;
		String errorMessage = null;
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
    			.path("login")
    			.parameter("instance", request.queryParams("instance"))
    			.parameter("username", request.queryParams("username"))
    			.parameter("password", request.queryParams("password"))
    			.parameter("securityToken", request.queryParams("securityToken"))
    			.execute();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			result = httpResponse.getEntity(LoginResult.class);
			
			httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("salesforce")
	    			.path("sobjects")
	    			.queryParameter("id", result.getId())
	    			.execute();

			if (httpResponse.getStatusCode() == Status.OK) {
				sobjects = httpResponse.getEntityList(Sobject.class);
			} else {
				errorMessage = httpResponse.getEntity(JsonNode.class).get("message").asText();
				
			}
		} else {
			errorMessage = httpResponse.getEntity(JsonNode.class).get("message").asText();
		}
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token.getAccessToken(), request.params(":id"));
    	
    	Optional<ServiceInstance> serviceInstance = salesforceConnector
    			.getServiceInstances()
    			.stream()
    			.filter(p -> p.getKey().equals(request.params(":key")))
    			.findFirst();
    	
    	Map<String, Object> model = new HashMap<String, Object>();
		model.put("labels", labels);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance.get());
		model.put("sobjects", sobjects);
		model.put("loginResult", result);
		model.put("errorMessage", errorMessage);
		
		return new ModelAndView(model, "secure/salesforce-outbound-message-part.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param respose
	 * @return
	 */
	
	private static String saveEnvironments(Request request, Response respose) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		String[] indexes = request.queryParamsValues("index");
		String[] names = request.queryParamsValues("name");
		String[] locked = request.queryParamsValues("locked");
		String[] label = request.queryParamsValues("label");
		
		Set<Environment> environments = new HashSet<Environment>();

		for (int i = 0; i < names.length; i++) {
			if (! names[i].trim().isEmpty()) {
				Environment environment = new Environment();
				environment.setIndex(Integer.valueOf(indexes[i]));
				environment.setName(names[i]);
				environment.setLabel(label[i]);
				environment.setActive(request.queryMap().get("active" + i).hasValue() ? Boolean.TRUE : Boolean.FALSE);
				environment.setLocked(Boolean.valueOf(locked[i]));
				environments.add(environment);
			}
		}
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
				.path("connector")
				.path(request.params(":id"))
				.path("service")
				.path(request.params(":key"))
				.path("environments")
				.body(environments)
    			.execute();
		
		String html = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			html = success(MessageProvider.getMessage(Locale.US, "saveSuccess"));
		} else {
			html = error(httpResponse.getEntity(JsonNode.class).get("message").asText());
		}
		
		return html;
	}
	
	/**
	 * 
	 * @param request
	 * @param respose
	 * @return
	 */
	
	private static ModelAndView saveConfiguration(Request request, Response respose) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
		
		String errorMessage = null;
		
		Set<String> sobjects = new HashSet<String>();
		Arrays.asList(request.queryMap("sobject").values()).stream().forEach(p -> {
			sobjects.add(p);
		});
		
		Map<String,Object> configParams = new HashMap<String,Object>();
		configParams.put("sobjects", sobjects);
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
				.path("connector")
				.path(request.params(":id"))
				.path("service")
				.path(request.params(":key"))
				.body(configParams)
    			.execute();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			//model.put("yaml", httpResponse.getHeaders().get("Location"));
		} else {
			errorMessage = httpResponse.getEntity(JsonNode.class).get("message").asText();
			
		}
		
		ServiceInstance serviceInstance = getServiceInstance(token.getAccessToken(), request.params(":id"), request.params(":key"));
				
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("serviceInstance", serviceInstance);
		model.put("sobjects", sobjects);
		model.put("errorMessage", errorMessage);
		
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
		
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
    	
    	SalesforceConnector salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
    	
    	return salesforceConnector;
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
		
    	ServiceInstance serviceInstance = httpResponse.getEntity(ServiceInstance.class);
    	
//    	Set<Environment> environments = serviceInstance.getEnvironments().stream().filter(p -> p.getName().equals(serviceInstance.getEnvironment())).collect(Collectors.toSet());
//    	
//    	serviceInstance.getEnvironments().clear();
//    	
//    	serviceInstance.getEnvironments().addAll(environments);
    	
    	return serviceInstance;
	}
}
package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

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
import com.nowellpoint.www.app.model.EnvironmentVariable;
import com.nowellpoint.www.app.model.SalesforceConnector;
import com.nowellpoint.www.app.model.ServiceInstance;
import com.nowellpoint.www.app.model.ServiceProvider;
import com.nowellpoint.www.app.model.sforce.LoginResult;
import com.nowellpoint.www.app.model.sforce.Sobject;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.RequestWrapper;

import org.slf4j.*;

//import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceConnectorController extends AbstractController {
	
	//private static final Logger LOGGER = Logger.getLogger(SalesforceConnectorController.class.getName());
	
	private final static Logger LOG = LoggerFactory.getLogger(SalesforceConnectorController.class.getName());
	
	public SalesforceConnectorController(Configuration cfg) {
		
		super(cfg);
		
		/**
		 * 
		 */
		
		get("/app/salesforce/oauth", (request, response) -> oauth(request, response));
        
        get("/app/salesforce/callback", (request, response) -> callback(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/salesforce/connector", (request, response) -> getSalesforceConnectorDetails(request, response), new FreeMarkerEngine(cfg));
        
        /**
         * 
         */
        
        get("/app/connectors/salesforce", (request, response) -> getSalesforceConnectors(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/connector", (request, response) -> saveSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/connector/:id", (request, response) -> getSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/connectors/salesforce/connector/:id", (request, response) -> deleteSalesforceConnector(request, response));
        
        get("/app/connectors/salesforce/connector/:id/providers", (request, response) -> getServiceProviders(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/connector/:id/service", (request, response) -> addService(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/connectors/salesforce/connector/:id/service/:key", (request, response) -> removeService(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/connector/:id/service/:key/sobjects", (request, response) -> getSobjects(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/connector/:id/service/:key/configuration", (request, response) -> saveConfiguration(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/connector/:id/service/:key/details", (request, response) -> getService(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/connector/:id/service/:key/environments", (request, response) -> getEnvironments(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/connector/:id/service/:key/variables", (request, response) -> getEnvironmentVariables(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/connector/:id/service/:key/variables/add", (request, response) -> addEnvironmentVariable(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/connector/:id/service/:key/environments", (request, response) -> saveEnvironments(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/connector/:id/service/:key/variables", (request, response) -> saveEnvironmentVariables(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/connector/:id/service/:key/variables/:environment", (request, response) -> getEnvironmentVariablesForInstance(request, response), new FreeMarkerEngine(cfg));
        
	}
	
	private static ModelAndView addEnvironmentVariable(Request request, Response response) {	
		return new ModelAndView(new HashMap<String, Object>(), "secure/fragments/environment-table-row.html");
	}
	
	private static ModelAndView removeService(Request request, Response response) {
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("salesforce")
				.path("connector")
				.path(request.params(":id"))
				.path("service")
				.path(request.queryParams("key"))
				.execute();
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("errorMessage", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
	}
	
	private static ModelAndView getEnvironmentVariablesForInstance(Request request, Response response) {
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, request.params(":id"));
		
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(request.params(":key")).get();

		Optional<Environment> environment = serviceInstance.getEnvironments()
				.stream()
				.filter(p -> p.getName().equals(request.params(":environment")))
				.findFirst();
		
		if (environment.isPresent() && environment.get().getEnvironmentVariables().size() == 0) {
			environment.get().getEnvironmentVariables().add(new EnvironmentVariable());
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("labels", labels);
		model.put("environment", environment.get());
		
		return new ModelAndView(model, "secure/fragments/instance-environment-variables-table.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getEnvironmentVariables(Request request, Response response) {
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		Account account = requestWrapper.getAccount();
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, request.params(":id"));
		
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(request.params(":key")).get();
		
		Optional<Environment> environment = serviceInstance.getEnvironments()
				.stream()
				.filter(p -> p.getName().equals(serviceInstance.getDefaultEnvironment()))
				.findFirst();
		
		if (serviceInstance.getEnvironmentVariables().size() == 0) {
			serviceInstance.getEnvironmentVariables().add(new EnvironmentVariable());
		}
		
		if (environment.isPresent() && environment.get().getEnvironmentVariables().size() == 0) {
			environment.get().getEnvironmentVariables().add(new EnvironmentVariable());
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
		model.put("environment", environment.get());
		
		return new ModelAndView(model, "secure/environment-variables.html");
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
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, id);
    	
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key).get();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
		
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
		
		String id = request.params(":id");
		String key = request.params(":key");
    	
    	SalesforceConnector salesforceConnector = getSalesforceConnector(token, id);
    	
    	ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key).get();
    	
    	Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
		model.put("sobjects", Collections.emptyList());
		model.put("loginResult", null);
		
		return new ModelAndView(model, "secure/".concat(serviceInstance.getConfigurationPage()));
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
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("errorMessage", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
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
    	
    	LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " : " + httpResponse.getHeaders().get("Location"));
		
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
			salesforceConnector = getSalesforceConnector(token, request.params(":id"));
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
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<SalesforceConnector> salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		
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
	
	private static ModelAndView saveSalesforceConnector(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("salesforce")
    			.path("connector")
    			.parameter("id", request.queryParams("id"))
    			.execute();
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());	
    	
		Map<String, Object> model = new HashMap<String, Object>();
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("errorMessage", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
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
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
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
			salesforceConnector = getSalesforceConnector(token, request.params(":id"));
		} catch (BadRequestException e) {
			errorMessage = e.getMessage();
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("labels", labels);
		model.put("serviceProviders", providers);
		model.put("id", salesforceConnector.getId());
		model.put("organizationName", salesforceConnector.getOrganization().getName());
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
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, request.params(":id"));
		
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(request.params(":key")).get();
    	
    	Map<String, Object> model = new HashMap<String, Object>();
		model.put("labels", labels);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
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
	
	private static ModelAndView saveEnvironments(Request request, Response respose) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		String[] indexes = request.queryParamsValues("index");
		String[] names = request.queryParamsValues("name");
		String[] locked = request.queryParamsValues("locked");
		String[] label = request.queryParamsValues("label");
		
		Set<Environment> environments = new HashSet<Environment>();

		for (int i = 0; i < names.length; i++) {
			if (names != null && ! names[i].trim().isEmpty()) {
				Environment environment = new Environment();
				environment.setIndex(Integer.valueOf(indexes[i]));
				environment.setName(names[i].toUpperCase());
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
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("errorMessage", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
	}
	
	private static ModelAndView saveEnvironmentVariables(Request request, Response response) {
		
		RequestWrapper requestWrapper = new RequestWrapper(request);
		
		Token token = requestWrapper.getToken();
		
		String id = request.params(":id");
		String key = request.params(":key");
		String defaultEnvironment = request.queryParams("defaultEnvironment");
		
		String[] locked = request.queryParamsValues("locked");
		String[] variables = request.queryParamsValues("variable");
		String[] values = request.queryParamsValues("value");
		
		Set<EnvironmentVariable> environmentVariables = null;
		
		if (variables != null) {
			environmentVariables = new HashSet<EnvironmentVariable>();
			for (int i = 0; i < variables.length; i++) {
				if (variables[i] != null && ! variables[i].trim().isEmpty()) {
					EnvironmentVariable environmentVariable = new EnvironmentVariable();
					environmentVariable.setLocked(Boolean.valueOf(locked[i]));
					environmentVariable.setValue(values[i]);
					environmentVariable.setVariable(variables[i].toUpperCase());
					environmentVariables.add(environmentVariable);
				}	
			}
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("labels", labels);
		
		if (environmentVariables == null) {
			model.put("errorMessage", MessageProvider.getMessage(Locale.US, "nothingToSave"));
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
		
		SalesforceConnector salesforceConnector = null;
		
		HttpResponse httpResponse = null;

		String successMessage = null;
		
		if (defaultEnvironment != null) {
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.path("salesforce")
					.path("connector")
					.path(id)
					.path("service")
					.path(key)
					.path("variables")
					.path(defaultEnvironment)
					.body(environmentVariables)
	    			.execute();
			
			response.status(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
				successMessage = MessageProvider.getMessage(Locale.US, "saveSuccess");
				
				salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
				
				ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key).get();
				
				Optional<Environment> environment = serviceInstance.getEnvironment(defaultEnvironment);
				
				if (environment.isPresent() && environment.get().getEnvironmentVariables().size() == 0) {
					environment.get().getEnvironmentVariables().add(new EnvironmentVariable());
				}
				
				model.put("successMessage", successMessage);
				model.put("serviceInstance", serviceInstance);
				model.put("environment", environment.get());
				
				return new ModelAndView(model, "secure/fragments/instance-environment-variables-table.html");
				
			} else {
				model.put("errorMessage", httpResponse.getEntity(JsonNode.class).get("message").asText());
				return new ModelAndView(model, "secure/fragments/error-message.html");
			}
			
		} else {
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.path("salesforce")
					.path("connector")
					.path(id)
					.path("service")
					.path(key)
					.path("variables")
					.body(environmentVariables)
	    			.execute();
			
			response.status(httpResponse.getStatusCode());		
			
			if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
				successMessage = MessageProvider.getMessage(Locale.US, "saveSuccess");
				
				salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
				
				ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key).get();
				
				if (serviceInstance.getEnvironmentVariables().size() == 0) {
					serviceInstance.getEnvironmentVariables().add(new EnvironmentVariable());
				}
				
				model.put("successMessage", successMessage);
				model.put("serviceInstance", serviceInstance);
				
				System.out.println(salesforceConnector.getId());
				
				return new ModelAndView(model, "secure/fragments/default-environment-variables-table.html");
				
			} else {				
				model.put("errorMessage", httpResponse.getEntity(JsonNode.class).get("message").asText());
				return new ModelAndView(model, "secure/fragments/error-message.html");
			}
		}
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
	
	private static SalesforceConnector getSalesforceConnector(Token token, String id) {
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
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
    	
    	return serviceInstance;

	}
}
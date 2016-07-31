package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Environment;
import com.nowellpoint.www.app.model.EnvironmentVariable;
import com.nowellpoint.www.app.model.Plan;
import com.nowellpoint.www.app.model.SalesforceConnector;
import com.nowellpoint.www.app.model.Service;
import com.nowellpoint.www.app.model.ServiceInstance;
import com.nowellpoint.www.app.model.ServiceProvider;
import com.nowellpoint.www.app.model.sforce.Field;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceConnectorController extends AbstractController {
	
	private final static Logger LOG = LoggerFactory.getLogger(SalesforceConnectorController.class.getName());
	
	public SalesforceConnectorController(Configuration cfg) {
		super(SalesforceConnectorController.class, cfg);
	}
	
	public void configureRoutes(Configuration cfg) {
		get("/app/connectors/salesforce", (request, response) -> getSalesforceConnectors(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce", (request, response) -> createSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id", (request, response) -> getSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/edit", (request, response) -> editSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/:id", (request, response) -> updateSalesforceConnector(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/connectors/salesforce/:id", (request, response) -> deleteSalesforceConnector(request, response));
        
        get("/app/connectors/salesforce/:id/providers/:serviceProviderId/service/:serviceType/plan/:code", (request, response) -> reviewPlan(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/:id/providers/:serviceProviderId/service/:serviceType/plan/:code", (request, response) -> addServiceInstance(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/providers", (request, response) -> getServiceProviders(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/:id/service/:key", (request, response) -> saveServiceInstance(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/connectors/salesforce/:id/service/:key", (request, response) -> deleteServiceInstance(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/sobjects", (request, response) -> getSobjects(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/listeners/:environment/fields/:sobject", (request, response) -> getFields(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/details", (request, response) -> getServiceInstance(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/environments", (request, response) -> getEnvironments(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/variables/add", (request, response) -> addEnvironmentVariable(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/:id/service/:key/environments", (request, response) -> saveEnvironments(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/:id/service/:key/variables", (request, response) -> saveEnvironmentVariables(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/variables", (request, response) -> getEnvironmentVariables(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/variables/:environment", (request, response) -> getEnvironmentVariables(request, response), new FreeMarkerEngine(cfg));        
        
        get("/app/connectors/salesforce/:id/service/:key/listeners", (request, response) -> getEventListeners(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/listeners/query", (request, response) -> testQuery(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/:id/service/:key/listeners", (request, response) -> saveEventListeners(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/connection/:environment", (request, response) -> testConnection(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/:id/service/:key/targets", (request, response) -> getTargets(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/:id/service/:key/targets", (request, response) -> saveTargets(request, response), new FreeMarkerEngine(cfg));
        
        post("/app/connectors/salesforce/:id/service/:key/deployment/:environment", (request, response) -> deploy(request, response), new FreeMarkerEngine(cfg));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView addEnvironmentVariable(Request request, Response response) {	
		return new ModelAndView(getModel(), "secure/fragments/environment-table-row.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getEventListeners(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, id);
		
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
			
		return new ModelAndView(model, "secure/event-listeners.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getTargets(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, id);
		
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
		model.put("targets", serviceInstance.getTargets());
			
		return new ModelAndView(model, "secure/targets.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView saveTargets(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String bucketName = request.queryParams("bucketName");
		String awsAccessKey = request.queryParams("awsAccessKey");
		String awsSecretAccessKey = request.queryParams("awsSecretAccessKey");
		
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode node = objectMapper.createObjectNode();
		node.set("simpleStorageService", objectMapper.createObjectNode()
				.put("bucketName", bucketName)
				.put("awsAccessKey", awsAccessKey)
				.put("awsSecretAccessKey", awsSecretAccessKey));
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.path("targets")
				.body(node)
				.execute();
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		Map<String, Object> model = getModel();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("message", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/e-message.html");
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView saveServiceInstance(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String name = request.queryParams("name");
		String sourceEnvironment = request.queryParams("sourceEnvironment");
		
		ObjectNode node = new ObjectMapper().createObjectNode()
				.put("name", name)
				.put("sourceEnvironment", sourceEnvironment);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.body(node)
				.execute();
		
		Map<String, Object> model = getModel();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("message", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/e-message.html");
		}
	}
	
	private ModelAndView deleteServiceInstance(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.execute();
		
		Map<String, Object> model = getModel();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "deleteSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("message", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/e-message.html");
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getEnvironmentVariables(Request request, Response response) {		
		Token token = getToken(request);
		Account account = getAccount(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String environmentName = request.params(":environment");
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, id);
		
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
		
		Environment environment = null;
		
		Map<String, Object> model = getModel();
		
		if (environmentName != null) {
			
			environment = serviceInstance.getEnvironments()
					.stream()
					.filter(p -> p.getName().equals(environmentName))
					.findFirst()
					.orElse(new Environment());
			
			if (environment.getEnvironmentVariables().size() == 0) {
				environment.getEnvironmentVariables().add(new EnvironmentVariable());
			}
			
			model.put("environment", environment);
			
			return new ModelAndView(model, "secure/fragments/environment-variables-table.html");
		} else {
			
			environment = serviceInstance.getEnvironments()
					.stream()
					.filter(p -> p.getName().equals(serviceInstance.getSourceEnvironment()))
					.findFirst()
					.orElse(new Environment());
			
			if (environment.getEnvironmentVariables().size() == 0) {
				environment.getEnvironmentVariables().add(new EnvironmentVariable());
			}
			
			model.put("account", account);
			model.put("salesforceConnector", salesforceConnector);
			model.put("serviceInstance", serviceInstance);
			model.put("environment", environment);
			
			return new ModelAndView(model, "secure/environment-variables.html");
		}	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getEnvironments(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, id);
    	
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
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
	
	private ModelAndView getServiceInstance(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
    	
    	SalesforceConnector salesforceConnector = getSalesforceConnector(token, id);
    	
    	ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
    	
    	Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
		
		return new ModelAndView(model, "secure/".concat(serviceInstance.getConfigurationPage()));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView addServiceInstance(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String serviceProviderId = request.params(":serviceProviderId");
		String serviceType = request.params(":serviceType");
		String code = request.params(":code");
		
		Map<String, Object> model = getModel();
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("providers")
				.path(serviceProviderId)
				.path("service")
				.path(serviceType)
				.path("plan")
				.path(code)
				.execute();
				
		if (httpResponse.getStatusCode() == Status.OK) {
			System.out.println("success");
			model.put("successMessage", getValue("saved.plan"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("message", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getSalesforceConnector(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
    	
		SalesforceConnector salesforceConnector = null;
		String message = null;
		
		try {
			salesforceConnector = getSalesforceConnector(token, request.params(":id"));
		} catch (BadRequestException e) {
			message = e.getMessage();
		}
		
		Map<String, Object> model = getModel();
    	model.put("account", account);
    	model.put("salesforceConnector", salesforceConnector);
    	model.put("message", message);
		
		return new ModelAndView(model, "secure/salesforce-connector.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView editSalesforceConnector(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
    	
		SalesforceConnector salesforceConnector = null;
		String message = null;
		
		try {
			salesforceConnector = getSalesforceConnector(token, request.params(":id"));
		} catch (BadRequestException e) {
			message = e.getMessage();
		}
		
		Map<String, Object> model = getModel();
    	model.put("account", account);
    	model.put("salesforceConnector", salesforceConnector);
    	model.put("message", message);
		
		return new ModelAndView(model, "secure/salesforce-connector-edit.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getSalesforceConnectors(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.execute();

		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<SalesforceConnector> salesforceConnectors = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		} else {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
    	model.put("account", account);
    	model.put("salesforceConnectorsList", salesforceConnectors);
    	
    	return new ModelAndView(model, "secure/salesforce-connectors-list.html");
    	
	}
	
	private ModelAndView updateSalesforceConnector(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String tag = request.queryParams("tag");
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.parameter("tag", tag)
    			.execute();
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());	
		
		return getSalesforceConnector(request, response);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView createSalesforceConnector(Request request, Response response) {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.parameter("id", request.queryParams("id"))
    			.execute();
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());	
		
		Map<String, Object> model = getModel();
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("message", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String deleteSalesforceConnector(Request request, Response response) {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
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
	
	private ModelAndView getServiceProviders(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		List<ServiceProvider> providers = Collections.emptyList();
		SalesforceConnector salesforceConnector = null;
		String message = null;
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.queryParameter("localeSidKey", "en_US")
				.queryParameter("languageLocaleKey", "en_US")
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			message = httpResponse.getAsString();
		} else {
			providers = httpResponse.getEntityList(ServiceProvider.class);
		}
		
		try {
			salesforceConnector = getSalesforceConnector(token, request.params(":id"));
		} catch (BadRequestException e) {
			message = e.getMessage();
		}
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("serviceProviders", providers);
		model.put("salesforceConnector", salesforceConnector);
		model.put("message", message);
    	
		return new ModelAndView(model, "secure/service-catalog.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView reviewPlan(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String id = request.params(":id");
		String serviceProviderId = request.params(":serviceProviderId");
		String serviceType = request.params(":serviceType");
		String code = request.params(":code");
		
		SalesforceConnector salesforceConnector = null;
		ServiceProvider provider = null;
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.path(serviceProviderId)
				.execute();
			
		if (httpResponse.getStatusCode() == Status.OK) {
			provider = httpResponse.getEntity(ServiceProvider.class);
		} else {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		salesforceConnector = getSalesforceConnector(token, id);
		
		Service service = provider.getServices()
				.stream()
				.filter(s -> s.getType().equals(serviceType))
				.findFirst()
				.get();
		
		Plan plan = service.getPlans()
				.stream()
				.filter(p -> p.getCode().equals(code))
				.findFirst()
				.get();
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("serviceProvider", provider);
		model.put("service", service);
		model.put("plan", plan);
		model.put("salesforceConnector", salesforceConnector);
    	
		return new ModelAndView(model, "secure/review-order.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getSobjects(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		String message = null;
		
		SalesforceConnector salesforceConnector = null;
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.path("sobjects")
    			.execute();
	
		response.status(httpResponse.getStatusCode());
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
		} else {
			message = httpResponse.getEntity(JsonNode.class).get("message").asText();
		}
		
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
    	
		Map<String, Object> model = getModel();
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
		model.put("message", message);
		
		return new ModelAndView(model, "secure/fragments/sobjects-table.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getFields(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String sobject = request.params(":sobject");
		
		String message = null;
		
		List<Field> fields = null;
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.path("sobjects")
				.path(sobject)
				.path("fields")
    			.execute();
	
		response.status(httpResponse.getStatusCode());
		
		if (httpResponse.getStatusCode() == Status.OK) {
			fields = httpResponse.getEntityList(Field.class);
		} else {
			message = httpResponse.getEntity(JsonNode.class).get("message").asText();
		}
		
		SalesforceConnector salesforceConnector = getSalesforceConnector(token, id);
		
		ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
		
		String query = serviceInstance
				.getEventListeners()
				.stream()
				.filter(p -> sobject.equals(p.getName()))
				.findFirst()
				.get()
				.getCallback();
		
		if (query == null || query.trim().length() == 0) {
			query = "Select %s From " + sobject;
			query = String.format(query, fields.stream().map(e -> e.getName()).collect(Collectors.joining(", ")));
		}
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("fields", fields);
		model.put("query", query);
		model.put("sobject", sobject);
		model.put("salesforceConnector", salesforceConnector);
		model.put("serviceInstance", serviceInstance);
		model.put("message", message);
		
		return new ModelAndView(model, "secure/query-edit.html");
	}
	
	private ModelAndView testQuery(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");

		String queryString = request.queryParams("callback");
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.get(API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("connectors")
					.path("salesforce")
					.path(id)
					.path("service")
					.path(key)
					.path("query")
					.queryParameter("q", URLEncoder.encode(queryString.concat(" Limit 1"), "UTF-8"))
					.execute();
			
		} catch (HttpRequestException | UnsupportedEncodingException e) {
			throw new InternalServerErrorException(e);
		} 
		
		Map<String, Object> model = getModel();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "testSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("message", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param respose
	 * @return
	 */
	
	private ModelAndView saveEnvironments(Request request, Response respose) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		String[] indexes = request.queryParamsValues("index");
		String[] names = request.queryParamsValues("name");
		String[] label = request.queryParamsValues("label");
		
		ArrayNode node = new ObjectMapper().createArrayNode();

		for (int i = 0; i < names.length; i++) {
			if (names != null && ! names[i].trim().isEmpty()) {
				node.addObject()
					.put("index", Integer.valueOf(indexes[i]))
					.put("name", names[i])
					.put("label", label[i])
					.put("active", request.queryMap().get("active" + i).hasValue() ? Boolean.TRUE : Boolean.FALSE);
			}
		}
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.path("environments")
				.body(node)
    			.execute();
		
		Map<String, Object> model = getModel();
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("message", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
	}
	
	private ModelAndView testConnection(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String environment = request.params(":environment");
		
		Map<String, Object> model = getModel();
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.path("connection")
				.path(environment)
    			.execute();
	
		response.status(httpResponse.getStatusCode());
		
		SalesforceConnector salesforceConnector = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
			
			ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
			
			model.put("serviceInstance", serviceInstance);
		} else {
			System.out.println(httpResponse.getAsString());
		}
		
		return new ModelAndView(model, "secure/fragments/environment-list.html");
	}
	
	private ModelAndView saveEnvironmentVariables(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String sourceEnvironment = request.queryParams("sourceEnvironment");
		
		Map<String, Object> model = getModel();
		
		String[] variables = request.queryParamsValues("variable");
		String[] values = request.queryParamsValues("value");
		
		ArrayNode node = new ObjectMapper().createArrayNode();

		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				if (variables != null && ! variables[i].trim().isEmpty()) {
					node.addObject()
						.put("value", values[i])
						.put("variable", variables[i])
						.put("encrypted", request.queryMap().get("encrypted" + i).hasValue() ? Boolean.TRUE : Boolean.FALSE);
				}
			}
		} else {
			model.put("message", MessageProvider.getMessage(Locale.US, "nothingToSave"));
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}

		SalesforceConnector salesforceConnector = null;

		String successMessage = null;

		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("connectors")
	    			.path("salesforce")
					.path(id)
					.path("service")
					.path(key)
					.path("variables")
					.path(sourceEnvironment)
					.body(node)
	    			.execute();
		
		response.status(httpResponse.getStatusCode());
		
		if (httpResponse.getStatusCode() == Status.OK || httpResponse.getStatusCode() == Status.CREATED) {
			successMessage = MessageProvider.getMessage(Locale.US, "saveSuccess");
				
			salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
				
			ServiceInstance serviceInstance = salesforceConnector.getServiceInstance(key);
				
			Optional<Environment> environment = serviceInstance.getEnvironment(sourceEnvironment);
				
			if (environment.isPresent() && environment.get().getEnvironmentVariables().size() == 0) {
				environment.get().getEnvironmentVariables().add(new EnvironmentVariable());
			}
				
			model.put("successMessage", successMessage);
			model.put("serviceInstance", serviceInstance);
			model.put("environment", environment.get());
			return new ModelAndView(model, "secure/fragments/success-message.html");
		} else {
			model.put("message", httpResponse.getEntity(JsonNode.class).get("message").asText());
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
	}
	
	private ModelAndView deploy(Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String environment = request.params(":environment");
		
		Map<String, Object> model = getModel();
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.path("deployment")
				.path(environment)
    			.execute();
		
		response.status(httpResponse.getStatusCode());
		
		return new ModelAndView(model, "secure/fragments/success-message.html");
	}
	
	private ModelAndView saveEventListeners(Request request, Response respose) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		Map<String, Object> model = getModel();
		
		String[] sobjects = request.queryParamsValues("sobject");
		
		if (sobjects == null) {
			model.put("message", MessageProvider.getMessage(Locale.US, "nothingToSave"));
			return new ModelAndView(model, "secure/fragments/error-message.html");
		}
		
		String[] callbacks = request.queryParamsValues("callback");
		
		List<String> create = new ArrayList<String>();
		List<String> update = new ArrayList<String>();
		List<String> delete = new ArrayList<String>();
		
		if (request.queryParamsValues("create") != null) {
			create = Arrays.asList(request.queryParamsValues("create"));
		}
		
		if (request.queryParamsValues("update") != null) {
			update = Arrays.asList(request.queryParamsValues("update"));
		}
		
		if (request.queryParamsValues("delete") != null) {
			delete = Arrays.asList(request.queryParamsValues("delete"));
		}
		
		ArrayNode node = new ObjectMapper().createArrayNode();
		
		for (int i = 0; i < sobjects.length; i++) {
			node.addObject()
				.put("name", sobjects[i])
				.put("create", create.contains(sobjects[i]))
				.put("update", update.contains(sobjects[i]))
				.put("delete", delete.contains(sobjects[i]))
				.put("callback", callbacks[i]);
		}
	
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("service")
				.path(key)
				.path("listeners")
				.body(node)
    			.execute();
		
		SalesforceConnector salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
		
		model.put("salesforceConnector", salesforceConnector);
		model.put("successMessage", MessageProvider.getMessage(Locale.US, "saveSuccess"));
		
		return new ModelAndView(model, "secure/fragments/success-message.html");
	}
	
	/**
	 * 
	 * @param accessToken
	 * @param id
	 * @return
	 */
	
	private SalesforceConnector getSalesforceConnector(Token token, String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.execute();
		
		SalesforceConnector salesforceConnector = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			throw new BadRequestException(httpResponse.getAsString());
		}
    	
    	return salesforceConnector;
	}
}
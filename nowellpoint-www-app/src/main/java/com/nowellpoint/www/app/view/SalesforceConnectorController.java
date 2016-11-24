package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.ExceptionResponse;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.sforce.ThemeItem;
import com.nowellpoint.client.model.sforce.Icon;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SalesforceConnectorController extends AbstractController {
	
	private static final Logger LOG = LoggerFactory.getLogger(SalesforceConnectorController.class.getName());
	
	public static class Template {
		public static final String SALESFORCE_CONNECTOR = String.format(APPLICATION_CONTEXT, "salesforce-connector.html");
		public static final String SALESFORCE_CONNECTOR_NEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-new.html");
		public static final String SALESFORCE_CONNECTOR_EDIT = String.format(APPLICATION_CONTEXT, "salesforce-connector-edit.html");
		public static final String SALESFORCE_CONNECTORS_LIST = String.format(APPLICATION_CONTEXT, "salesforce-connectors-list.html");
		public static final String ENVIRONMENT = String.format(APPLICATION_CONTEXT, "environment.html");
		public static final String ENVIRONMENTS = String.format(APPLICATION_CONTEXT, "environments.html");
		public static final String SOBJECTS = String.format(APPLICATION_CONTEXT, "sobjects.html");
		public static final String TARGETS = String.format(APPLICATION_CONTEXT, "targets.html");
	}
	
	public SalesforceConnectorController() {
		super(SalesforceConnectorController.class);      
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.CONNECTORS_SALESFORCE_LIST, (request, response) -> getSalesforceConnectors(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_NEW, (request, response) -> newSalesforceConnector(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_VIEW, (request, response) -> viewSalesforceConnector(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_UPDATE, (request, response) -> updateSalesforceConnector(configuration, request, response));
        delete(Path.Route.CONNECTORS_SALESFORCE_DELETE, (request, response) -> deleteSalesforceConnector(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_EDIT, (request, response) -> editSalesforceConnector(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_NEW, (request, response) -> newEnvironment(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_VIEW, (request, response) -> viewEnvironment(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_EDIT, (request, response) -> editEnvironment(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_ADD, (request, response) -> addEnvironment(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_UPDATE, (request, response) -> updateEnvironment(configuration, request, response));
        delete(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_REMOVE, (request, response) -> removeEnvironment(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_TEST, (request, response) -> testConnection(configuration, request, response));  
        post(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_BUILD, (request, response) -> buildEnvironment(configuration, request, response));  
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_SOBJECTS, (request, response) -> sobjectsList(configuration, request, response));
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * newSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String newSalesforceConnector(Configuration configuration, Request request, Response response) {		
		String id = request.params(":id");
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		
		return render(configuration, request, response, model, Template.SALESFORCE_CONNECTOR_NEW);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * newEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String newEnvironment(Configuration configuration, Request request, Response response) {	
		String id = request.params(":id");
		
		Environment environment = new Environment();
		environment.setAuthEndpoint("https://test.salesforce.com");
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "add");
		model.put("action", String.format("/app/connectors/salesforce/%s/environments", id));
		model.put("environment", environment);
		
		return render(configuration, request, response, model, Template.ENVIRONMENT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * sobjectsList
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String sobjectsList(Configuration configuration, Request request, Response response) {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		GetResult<Environment> result = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.get(id, key);
		
		Environment environment = result.getTarget();
		
		Map<String,String> icons = new HashMap<String,String>();
		
		environment.getSobjects().stream().forEach(sobject -> {
			Optional<ThemeItem> item = environment.getTheme()
					.getThemeItems()
					.stream()
					.filter(themeItem -> themeItem.getName().equals(sobject.getName()))
					.findFirst();
			
			if (item.isPresent()) {
				Optional<Icon> icon = item.get()
						.getIcons()
						.stream()
						.filter(i -> i.getHeight() == 32)
						.findFirst();
				
				if (icon.isPresent()) {
					icons.put(sobject.getName(), icon.get().getUrl());
				} 
			}
		});
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("environment", environment);
		model.put("icons", icons);
		
		return render(configuration, request, response, model, Template.SOBJECTS);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * viewEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String viewEnvironment(Configuration configuration, Request request, Response response) {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		GetResult<Environment> result = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.get(id, key);
		
		Environment environment = result.getTarget();
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "view");
		model.put("environment", environment);
		
		return render(configuration, request, response, model, Template.ENVIRONMENT);
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private String editEnvironment(Configuration configuration, Request request, Response response) {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.path("environment")
    			.path(key)
    			.execute();
		
		Environment environment = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			environment = httpResponse.getEntity(Environment.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "edit");
		model.put("action", String.format("/app/connectors/salesforce/%s/environments/%s", id, key));
		model.put("environment", environment);
		
		return render(configuration, request, response, model, Template.ENVIRONMENT);
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * addEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private String addEnvironment(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String active = request.queryParams("active");
		String authEndpoint = request.queryParams("authEndpoint");
		String name = request.queryParams("environmentName");
		String password = request.queryParams("password");
		String username = request.queryParams("username");
		String securityToken = request.queryParams("securityToken");
		
		Environment environment = new Environment()
				.withIsActive(Boolean.valueOf(active))
				.withAuthEndpoint(authEndpoint)
				.withEnvironmentName(name)
				.withPassword(password)
				.withUsername(username)
				.withSecurityToken(securityToken);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.path("environment")
    			.body(environment)
				.execute();

		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			Map<String, Object> model = getModel();
			model.put("id", id);
			model.put("mode", "add");
			model.put("action", String.format("/app/connectors/salesforce/%s/environments", id));
			model.put("environment", environment);
			model.put("errorMessage", error.getMessage());
			
			String output = render(configuration, request, response, model, Template.ENVIRONMENT);
			
			throw new BadRequestException(output);
		}

		response.cookie(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id), "successMessage", MessageProvider.getMessage(getLocale(request), "add.environment.success"), 3, Boolean.FALSE);
		response.redirect(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		
		return "";		
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private String updateEnvironment(Configuration configuration, Request request, Response response) {	
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String active = request.queryParams("active");
		String authEndpoint = request.queryParams("authEndpoint");
		String environmentName = request.queryParams("environmentName");
		String password = request.queryParams("password");
		String username = request.queryParams("username");
		String securityToken = request.queryParams("securityToken");
		
		Environment environment = new Environment()
				.withIsActive(Boolean.valueOf(active))
				.withAuthEndpoint(authEndpoint)
				.withEnvironmentName(environmentName)
				.withPassword(password)
				.withUsername(username)
				.withSecurityToken(securityToken);
		
		HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.path("environment")
    			.path(key)
    			.body(environment)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			Map<String, Object> model = getModel();
			model.put("id", id);
			model.put("mode", "edit");
			model.put("action", String.format("/app/connectors/salesforce/%s/environments/%s", id, key));
			model.put("environment", environment);
			model.put("errorMessage", error.getMessage());
			
			String output = render(configuration, request, response, model, Template.ENVIRONMENT);
			
			throw new BadRequestException(output);
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "update.environment.success"), 3);
		response.redirect(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		
		return "";		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * removeEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String removeEnvironment(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.path("environment")
    			.path(key)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new BadRequestException(error.getMessage());
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "remove.environment.success"), 3);
		response.header("Location", Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String viewSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		SalesforceConnector salesforceConnector = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.get(id);
		
		String createdByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", salesforceConnector.getCreatedBy().getId());
		String lastModifiedByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", salesforceConnector.getLastModifiedBy().getId());
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnector", salesforceConnector);
    	model.put("successMessage", request.cookie("successMessage"));
    	model.put("createdByHref", createdByHref);
    	model.put("lastModifiedByHref", lastModifiedByHref);
		
    	return render(configuration, request, response, model, Template.SALESFORCE_CONNECTOR);
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private String editSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String view = request.queryParams("view");
    	
		SalesforceConnector salesforceConnector = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.get(id);
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnector", salesforceConnector);
    	
    	if (view != null && view.equals("1")) {
			model.put("cancel", Path.Route.CONNECTORS_SALESFORCE_LIST);
		} else {
			model.put("cancel", Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		}
		
    	return render(configuration, request, response, model, Template.SALESFORCE_CONNECTOR_EDIT);
	};

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	private String getSalesforceConnectors(Configuration configuration, Request request, Response response) {	
		Token token = getToken(request);
		
		List<SalesforceConnector> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.getSalesforceConnectors();
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnectorsList", salesforceConnectors);
    	
    	return render(configuration, request, response, model, Template.SALESFORCE_CONNECTORS_LIST);
    	
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private String updateSalesforceConnector(Configuration configuration, Request request, Response response) {	
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
		
		SalesforceConnector salesforceConnector = null;
		String message = null;
		
		try {
			salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
		} catch (BadRequestException e) {
			
			Map<String, Object> model = getModel();
	    	model.put("salesforceConnector", salesforceConnector);
	    	model.put("successMessage", request.cookie("successMessage"));
	    	model.put("errorMessage", message);
			
	    	return render(configuration, request, response, model, Template.SALESFORCE_CONNECTOR);
			
		}
		
		response.redirect(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", salesforceConnector.getId()));
		
		return "";
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * deleteSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	private String deleteSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(request.params(":id"))
    			.execute();
		
		LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		return "";
	};
	
	private String buildEnvironment(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		UpdateResult<Environment> updateResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.build(id, key);
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 */

	private String testConnection(Configuration configuration, Request request, Response response) throws JsonProcessingException {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
				.path(id)
				.path("environment")
    			.path(key)
				.parameter("test", Boolean.TRUE.toString())
    			.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Environment environment = httpResponse.getEntity(Environment.class);
		
		if (environment.getIsValid()) {
			environment.setTestMessage(MessageProvider.getMessage(getLocale(request), "test.connection.success"));
		} else {
			environment.setTestMessage(String.format("%s: %s", MessageProvider.getMessage(getLocale(request), "test.connection.fail"), environment.getTestMessage()));
		}
		
		return objectMapper.writeValueAsString(environment);
	};
}
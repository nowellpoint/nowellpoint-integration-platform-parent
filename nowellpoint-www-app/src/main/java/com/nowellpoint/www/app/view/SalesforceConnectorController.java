package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.EnvironmentRequest;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.SObjectDetail;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorRequest;
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
	
	public static class Template {
		public static final String SALESFORCE_CONNECTOR = String.format(APPLICATION_CONTEXT, "salesforce-connector.html");
		public static final String SALESFORCE_CONNECTOR_NEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-new.html");
		public static final String SALESFORCE_CONNECTOR_EDIT = String.format(APPLICATION_CONTEXT, "salesforce-connector-edit.html");
		public static final String SALESFORCE_CONNECTORS_LIST = String.format(APPLICATION_CONTEXT, "salesforce-connectors-list.html");
		public static final String ENVIRONMENT = String.format(APPLICATION_CONTEXT, "environment.html");
		public static final String ENVIRONMENTS = String.format(APPLICATION_CONTEXT, "environments.html");
		public static final String SOBJECTS = String.format(APPLICATION_CONTEXT, "sobject-list.html");
		public static final String SOBJECT_DETAIL = String.format(APPLICATION_CONTEXT, "sobject-detail.html");
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
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_SOBJECT_DETAIL, (request, response) -> getSobjectDetail(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String newSalesforceConnector(Configuration configuration, Request request, Response response) {		
		String id = request.params(":id");
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		
		return render(configuration, request, response, model, Template.SALESFORCE_CONNECTOR_NEW);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
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
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String getSobjectDetail(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String sobjectName = request.params(":sobjectName");
		
		GetResult<SObjectDetail> getSobjectDetailResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.sobjectDetail()
				.get(id, key, sobjectName);
		
		SObjectDetail sobjectDetail = getSobjectDetailResult.getTarget();
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("key", key);
		model.put("sobjectDetail", sobjectDetail);
		
		return render(configuration, request, response, model, Template.SOBJECT_DETAIL);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
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
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
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
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	private String editEnvironment(Configuration configuration, Request request, Response response) {		
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
		model.put("mode", "edit");
		model.put("action", String.format("/app/connectors/salesforce/%s/environments/%s", id, key));
		model.put("environment", environment);
		
		return render(configuration, request, response, model, Template.ENVIRONMENT);
	};

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String addEnvironment(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String active = request.queryParams("active");
		String authEndpoint = request.queryParams("authEndpoint");
		String environmentName = request.queryParams("environmentName");
		String password = request.queryParams("password");
		String username = request.queryParams("username");
		String securityToken = request.queryParams("securityToken");
		
		EnvironmentRequest environmentRequest = new EnvironmentRequest()
				.withIsActive(Boolean.valueOf(active))
				.withAuthEndpoint(authEndpoint)
				.withEnvironmentName(environmentName)
				.withPassword(password)
				.withUsername(username)
				.withSecurityToken(securityToken);
		
		CreateResult<Environment> createResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.add(id, environmentRequest);

		if (! createResult.isSuccess()) {
			
			Environment environment = new Environment()
					.withIsActive(Boolean.valueOf(active))
					.withAuthEndpoint(authEndpoint)
					.withEnvironmentName(environmentName)
					.withPassword(password)
					.withUsername(username)
					.withSecurityToken(securityToken);
			
			Map<String, Object> model = getModel();
			model.put("id", id);
			model.put("mode", "add");
			model.put("action", String.format("/app/connectors/salesforce/%s/environments", id));
			model.put("environment", environment);
			model.put("errorMessage", createResult.getErrorMessage());
			
			String output = render(configuration, request, response, model, Template.ENVIRONMENT);
			
			throw new BadRequestException(output);
		}

		response.cookie(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id), "successMessage", MessageProvider.getMessage(getLocale(request), "add.environment.success"), 3, Boolean.FALSE);
		response.redirect(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		
		return "";		
	};

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
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
		
		EnvironmentRequest environmentRequest = new EnvironmentRequest()
				.withIsActive(Boolean.valueOf(active))
				.withAuthEndpoint(authEndpoint)
				.withEnvironmentName(environmentName)
				.withPassword(password)
				.withUsername(username)
				.withSecurityToken(securityToken);
		
		UpdateResult<Environment> updateResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.update(id, key, environmentRequest);
		
		if (! updateResult.isSuccess()) {
			
			Environment environment = new Environment()
					.withIsActive(Boolean.valueOf(active))
					.withAuthEndpoint(authEndpoint)
					.withEnvironmentName(environmentName)
					.withPassword(password)
					.withUsername(username)
					.withSecurityToken(securityToken);
			
			Map<String, Object> model = getModel();
			model.put("id", id);
			model.put("mode", "edit");
			model.put("action", String.format("/app/connectors/salesforce/%s/environments/%s", id, key));
			model.put("environment", environment);
			model.put("errorMessage", updateResult.getErrorMessage());
			
			String output = render(configuration, request, response, model, Template.ENVIRONMENT);
			
			throw new BadRequestException(output);
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "update.environment.success"), 3);
		response.redirect(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		
		return "";		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String removeEnvironment(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		DeleteResult deleteResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.delete(id, key);
		
		if (! deleteResult.isSuccess()) {
			throw new BadRequestException(deleteResult.getErrorMessage());
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "remove.environment.success"), 3);
		response.header("Location", Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		
		return "";
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String viewSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		GetResult<SalesforceConnector> getResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.get(id);
		
		SalesforceConnector salesforceConnector = getResult.getTarget();
		
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
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	private String editSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String view = request.queryParams("view");
    	
		GetResult<SalesforceConnector> getResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.get(id);
		
		SalesforceConnector salesforceConnector = getResult.getTarget();
		
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
		
		GetResult<List<SalesforceConnector>> getResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.getSalesforceConnectors();
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnectorsList", getResult.getTarget());
    	
    	return render(configuration, request, response, model, Template.SALESFORCE_CONNECTORS_LIST);
    	
	};

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	private String updateSalesforceConnector(Configuration configuration, Request request, Response response) {	
		Token token = getToken(request);
		
		String id = request.params(":id");
		String tag = request.queryParams("tag");
		
		SalesforceConnectorRequest salesforceConnectorRequest = new SalesforceConnectorRequest()
				.withTag(tag);
		
		UpdateResult<SalesforceConnector> updateResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.update(id, salesforceConnectorRequest);
		
		String message = null;
		
		if (! updateResult.isSuccess()) {
			
			GetResult<SalesforceConnector> getResult = new NowellpointClient(new TokenCredentials(token))
					.salesforceConnector()
					.get(id);
			
			SalesforceConnector salesforceConnector = getResult.getTarget();
			
			Map<String, Object> model = getModel();
	    	model.put("salesforceConnector", salesforceConnector);
	    	model.put("errorMessage", message);
			
	    	return render(configuration, request, response, model, Template.SALESFORCE_CONNECTOR);
		}

		response.redirect(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		
		return "";
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	private String deleteSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		DeleteResult deleteResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.delete(id);
		
		if (! deleteResult.isSuccess()) {
			throw new BadRequestException(deleteResult.getErrorMessage());
		}
		
		return "";
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 */
	
	private String buildEnvironment(Configuration configuration, Request request, Response response) throws JsonProcessingException {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		UpdateResult<Environment> updateResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.build(id, key);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
			return String.format("%s: %s", MessageProvider.getMessage(getLocale(request), "test.connection.fail"), updateResult.getErrorMessage());
		}
		
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
		
		UpdateResult<Environment> updateResult = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.environment()
				.test(id, key);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
			return String.format("%s: %s", MessageProvider.getMessage(getLocale(request), "test.connection.fail"), updateResult.getErrorMessage());
		}
		
		return "";
	};
}
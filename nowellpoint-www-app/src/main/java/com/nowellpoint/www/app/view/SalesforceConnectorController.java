package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Instance;
import com.nowellpoint.client.model.EnvironmentRequest;
import com.nowellpoint.client.model.SObjectDetail;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
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
		public static final String SALESFORCE_INSTANCE = String.format(APPLICATION_CONTEXT, "salesforce-instance.html");
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
        get(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_NEW, (request, response) -> newInstance(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_VIEW, (request, response) -> viewInstance(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_EDIT, (request, response) -> editInstance(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_ADD, (request, response) -> addInstance(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_UPDATE, (request, response) -> updateInstance(configuration, request, response));
        delete(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_REMOVE, (request, response) -> removeInstance(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_TEST, (request, response) -> testConnection(configuration, request, response));  
        post(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_BUILD, (request, response) -> buildInstance(configuration, request, response));  
        get(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_SOBJECTS, (request, response) -> sobjectsList(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_INSTANCE_SOBJECT_DETAIL, (request, response) -> getSobjectDetail(configuration, request, response));
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
	
	private String newInstance(Configuration configuration, Request request, Response response) {	
		String id = request.params(":id");
		
		Instance instance = new Instance();
		instance.setAuthEndpoint("https://test.salesforce.com");
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "add");
		model.put("action", String.format("/app/connectors/salesforce/%s/instances", id));
		model.put("instance", instance);
		
		return render(configuration, request, response, model, Template.SALESFORCE_INSTANCE);
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
		
		SObjectDetail sobjectDetail = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.sobjectDetail()
				.get(id, key, sobjectName);
		
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
		
		Instance instance = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.get(id, key);
		
		Map<String,String> icons = new HashMap<String,String>();
		
		instance.getSobjects().stream().forEach(sobject -> {
			Optional<ThemeItem> item = instance.getTheme()
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
		model.put("instance", instance);
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
	
	private String viewInstance(Configuration configuration, Request request, Response response) {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		Instance instance = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.get(id, key);
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "view");
		model.put("instance", instance);
		
		return render(configuration, request, response, model, Template.SALESFORCE_INSTANCE);
	};

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	private String editInstance(Configuration configuration, Request request, Response response) {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		Instance instance = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.get(id, key);
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "edit");
		model.put("action", String.format("/app/connectors/salesforce/%s/environments/%s", id, key));
		model.put("instance", instance);
		
		return render(configuration, request, response, model, Template.SALESFORCE_INSTANCE);
	};

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String addInstance(Configuration configuration, Request request, Response response) {
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
		
		CreateResult<Instance> createResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.add(id, environmentRequest);

		if (! createResult.isSuccess()) {
			
			Instance instance = new Instance()
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
			model.put("instance", instance);
			model.put("errorMessage", createResult.getErrorMessage());
			
			String output = render(configuration, request, response, model, Template.SALESFORCE_INSTANCE);
			
			throw new BadRequestException(output);
		}

		response.cookie(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id), "successMessage", MessageProvider.getMessage(getLocale(request), "add.instance.success"), 3, Boolean.FALSE);
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

	private String updateInstance(Configuration configuration, Request request, Response response) {	
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
		
		UpdateResult<Instance> updateResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.update(id, key, environmentRequest);
		
		if (! updateResult.isSuccess()) {
			
			Instance instance = new Instance()
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
			model.put("instance", instance);
			model.put("errorMessage", updateResult.getErrorMessage());
			
			String output = render(configuration, request, response, model, Template.SALESFORCE_INSTANCE);
			
			throw new BadRequestException(output);
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "update.instance.success"), 3);
		response.redirect(Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id).concat("#instances"));
		
		return "";		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String removeInstance(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		DeleteResult deleteResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.delete(id, key);
		
		if (! deleteResult.isSuccess()) {
			throw new BadRequestException(deleteResult.getErrorMessage());
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "remove.instance.success"), 3);
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
		
		SalesforceConnector salesforceConnector = new NowellpointClient(token)
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
    	
		SalesforceConnector salesforceConnector = new NowellpointClient(token)
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
		
		SalesforceConnectorList salesforceConnectors = new NowellpointClient(token)
				.salesforceConnector()
				.getSalesforceConnectors();
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnectorsList", salesforceConnectors.getItems());
    	
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
		String name = request.queryParams("name");
		
		SalesforceConnectorRequest salesforceConnectorRequest = new SalesforceConnectorRequest()
				.withName(name)
				.withTag(tag);
		
		UpdateResult<SalesforceConnector> updateResult = new NowellpointClient(token)
				.salesforceConnector()
				.update(id, salesforceConnectorRequest);
		
		String message = null;
		
		if (! updateResult.isSuccess()) {
			
			SalesforceConnector salesforceConnector = new NowellpointClient(token)
					.salesforceConnector()
					.get(id);
			
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
		
		DeleteResult deleteResult = new NowellpointClient(token)
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
	 */
	
	private String buildInstance(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		UpdateResult<Instance> updateResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
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
		
		UpdateResult<Instance> updateResult = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.test(id, key);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
			return String.format("%s: %s", MessageProvider.getMessage(getLocale(request), "test.connection.fail"), updateResult.getErrorMessage());
		}
		
		return "";
	};
}
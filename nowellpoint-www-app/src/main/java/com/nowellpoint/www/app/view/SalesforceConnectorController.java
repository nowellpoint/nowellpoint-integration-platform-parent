package com.nowellpoint.www.app.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.JobTypeList;
import com.nowellpoint.client.model.SObject;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.SalesforceConnectorRequest;
import com.nowellpoint.client.model.ServiceRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.sforce.Icon;
import com.nowellpoint.client.model.sforce.ThemeItem;
import com.nowellpoint.www.app.util.Path;
import com.nowellpoint.www.app.util.TemplateBuilder;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SalesforceConnectorController extends AbstractStaticController {
	
	public static class Template {
		public static final String SALESFORCE_CONNECTOR_MAIN = String.format(APPLICATION_CONTEXT, "salesforce-connector-main.html");
		public static final String SALESFORCE_CONNECTOR_VIEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-view.html");
		public static final String SALESFORCE_CONNECTOR_NEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-new.html");
		public static final String SALESFORCE_CONNECTOR_EDIT = String.format(APPLICATION_CONTEXT, "salesforce-connector-edit.html");
		public static final String SALESFORCE_CONNECTOR_LIST = String.format(APPLICATION_CONTEXT, "salesforce-connector-list.html");
		public static final String SALESFORCE_CONNECTOR_SOBJECT_LIST = String.format(APPLICATION_CONTEXT, "salesforce-connector-sobject-list.html");
		public static final String SALESFORCE_CONNECTOR_SOBJECT_VIEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-sobject-view.html");
		public static final String SALESFORCE_CONNECTOR_ADD_SERVICE = String.format(APPLICATION_CONTEXT, "salesforce-connector-add-service.html");
		public static final String SALESFORCE_CONNECTOR_FLOW_NEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-flow-new.html");
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	public static String routeToSalesforceConnectors(Configuration configuration, Request request, Response response) {	    	
		return TemplateBuilder.template()
				.withConfiguration(configuration)
				.withControllerClass(SalesforceConnectorController.class)
				.withIdentity(getIdentity(request))
				.withLocale(getLocale(request))
				.withModel(getModel())
				.withTemplateName(Template.SALESFORCE_CONNECTOR_MAIN)
				.withTimeZone(getTimeZone(request))
				.build();
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String newSalesforceConnector(Configuration configuration, Request request, Response response) {		
		String id = request.params(":id");
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		
		return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_NEW);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String viewSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		SalesforceConnector salesforceConnector = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.get(id);
		
		Map<String,String> icons = new HashMap<String,String>();
		
		salesforceConnector.getSobjects().stream().forEach(sobject -> {
			Optional<ThemeItem> item = salesforceConnector.getTheme()
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
				} else {
					icons.put(sobject.getName(), "/images/sobject.png");
				}
			} else {
				icons.put(sobject.getName(), "/images/sobject.png");
			}
		});
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnector", salesforceConnector);
    	model.put("icons", icons);
		
    	return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_VIEW);
	};

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	public static String editSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String view = request.queryParams("view");
    	
		SalesforceConnector salesforceConnector = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.get(id);
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnector", salesforceConnector);
    	
    	if (view != null && view.equals("1")) {
			model.put("cancel", Path.Route.CONNECTORS_SALESFORCE_MAIN);
		} else {
			model.put("cancel", Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", id));
		}
		
    	return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_EDIT);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String listSalesforceConnectors(Configuration configuration, Request request, Response response) {	
		Token token = getToken(request);
		
		SalesforceConnectorList salesforceConnectorList = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.getSalesforceConnectors();
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnectorsList", salesforceConnectorList.getItems());
    	
    	return TemplateBuilder.template()
				.withConfiguration(configuration)
				.withControllerClass(SalesforceConnectorController.class)
				.withIdentity(getIdentity(request))
				.withLocale(getLocale(request))
				.withModel(model)
				.withTemplateName(Template.SALESFORCE_CONNECTOR_LIST)
				.withTimeZone(getTimeZone(request))
				.build();
	}

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */

	public static String updateSalesforceConnector(Configuration configuration, Request request, Response response) {	
		Token token = getToken(request);
		
		String id = request.params(":id");
		String tag = request.queryParams("tag");
		String name = request.queryParams("name");
		
		SalesforceConnectorRequest salesforceConnectorRequest = new SalesforceConnectorRequest()
				.withName(name)
				.withTag(tag);
		
		UpdateResult<SalesforceConnector> updateResult = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.update(id, salesforceConnectorRequest);
		
		String message = null;
		
		if (! updateResult.isSuccess()) {
			
			SalesforceConnector salesforceConnector = NowellpointClient.defaultClient(token)
					.salesforceConnector()
					.get(id);
			
			Map<String, Object> model = getModel();
	    	model.put("salesforceConnector", salesforceConnector);
	    	model.put("errorMessage", message);
			
	    	return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_VIEW);
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

	public static String deleteSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		DeleteResult deleteResult = NowellpointClient.defaultClient(token)
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
	
	public static String testSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<SalesforceConnector> updateResult = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.test(id);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String buildSalesforceConnector(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<SalesforceConnector> updateResult = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.build(id);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
		
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String listSObjects(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		SalesforceConnector salesforceConnector = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.get(id);
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnector", salesforceConnector);

		return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_SOBJECT_LIST);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String viewSObject(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String sobjectName = request.params(":sobjectName");
		
		SObject sobject = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.sobject()
				.get(id, sobjectName);
		
		SalesforceConnector salesforceConnector = new SalesforceConnector(id);
		
		Map<String, Object> model = getModel();
		model.put("salesforceConnector", salesforceConnector);
    	model.put("sobject", sobject);

		return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_SOBJECT_VIEW);
	}
	
	public static String newFlow(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		SalesforceConnector salesforceConnector = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.get(id);
		
		Map<String, Object> model = getModel();
		model.put("salesforceConnector", salesforceConnector);
		
		return TemplateBuilder.template()
				.withConfiguration(configuration)
				.withControllerClass(SalesforceConnectorController.class)
				.withIdentity(getIdentity(request))
				.withLocale(getLocale(request))
				.withModel(model)
				.withTemplateName(Template.SALESFORCE_CONNECTOR_FLOW_NEW)
				.withTimeZone(getTimeZone(request))
				.build();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String listServices(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		JobTypeList jobTypeList = NowellpointClient.defaultClient(token)
				.scheduledJobType()
				.getScheduledJobTypesByLanguage(identity.getLanguageSidKey());
		
		Map<String, Object> model = getModel();
		model.put("salesforceConnector", new SalesforceConnector(id));
    	model.put("jobTypeList", jobTypeList.getItems());

		return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_ADD_SERVICE);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String addService(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String serviceId = request.params(":serviceId");
		
		ServiceRequest serviceRequest = new ServiceRequest().withId(id)
				.withServiceId(serviceId);
		
		UpdateResult<SalesforceConnector> updateResult = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.service()
				.addService(serviceRequest);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
	}
}
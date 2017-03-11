package com.nowellpoint.www.app.view;

import java.util.Map;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.SObjectDetail;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.SalesforceConnectorRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SalesforceConnectorController extends AbstractStaticController {
	
	public static class Template {
		public static final String SALESFORCE_CONNECTOR_VIEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-view.html");
		public static final String SALESFORCE_CONNECTOR_NEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-new.html");
		public static final String SALESFORCE_CONNECTOR_EDIT = String.format(APPLICATION_CONTEXT, "salesforce-connector-edit.html");
		public static final String SALESFORCE_CONNECTOR_LIST = String.format(APPLICATION_CONTEXT, "salesforce-connector-list.html");
		public static final String SALESFORCE_CONNECTOR_SOBJECT_LIST = String.format(APPLICATION_CONTEXT, "salesforce-connector-sobject-list.html");
		public static final String SALESFORCE_CONNECTOR_SOBJECT_VIEW = String.format(APPLICATION_CONTEXT, "salesforce-connector-sobject-view.html");
	}
	
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
		
		SalesforceConnector salesforceConnector = new NowellpointClient(token)
				.salesforceConnector()
				.get(id);
		
		String createdByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", salesforceConnector.getCreatedBy().getId());
		String lastModifiedByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", salesforceConnector.getLastUpdatedBy().getId());
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnector", salesforceConnector);
    	model.put("createdByHref", createdByHref);
    	model.put("lastModifiedByHref", lastModifiedByHref);
		
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
		
		SalesforceConnectorList salesforceConnectors = new NowellpointClient(token)
				.salesforceConnector()
				.getSalesforceConnectors();
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnectorsList", salesforceConnectors.getItems());
    	
    	return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_LIST);
    	
	};

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
		
		DeleteResult deleteResult = new NowellpointClient(token)
				.salesforceConnector()
				.delete(id);
		
		if (! deleteResult.isSuccess()) {
			throw new BadRequestException(deleteResult.getErrorMessage());
		}
		
		return "";
	};
	
	public static String listSObjects(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		SalesforceConnector salesforceConnector = new NowellpointClient(token)
				.salesforceConnector()
				.get(id);
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnector", salesforceConnector);

		return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_SOBJECT_LIST);
	}
	
	public static String viewSObject(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String sobjectName = request.params(":sobjectName");
		
		SObjectDetail sobject = new NowellpointClient(token)
				.salesforceConnector().sobject()
				.get(id, sobjectName);
		
		SalesforceConnector salesforceConnector = new SalesforceConnector(id);
		
		Map<String, Object> model = getModel();
		model.put("salesforceConnector", salesforceConnector);
    	model.put("sobject", sobject);

		return render(SalesforceConnectorController.class, configuration, request, response, model, Template.SALESFORCE_CONNECTOR_SOBJECT_VIEW);
	}
}
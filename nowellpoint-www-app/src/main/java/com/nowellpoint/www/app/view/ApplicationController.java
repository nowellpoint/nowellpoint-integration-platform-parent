package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.Application;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class ApplicationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationController.class.getName());
	
	public static class Template {
		public static final String APPLICATION = String.format(APPLICATION_CONTEXT, "application.html");
		public static final String APPLICATION_CONNECTOR_SELECT = String.format(APPLICATION_CONTEXT, "application-connector-select.html");
		public static final String APPLICATION_EDIT = String.format(APPLICATION_CONTEXT, "application-edit.html");
		public static final String APPLICATION_CREATE = String.format(APPLICATION_CONTEXT, "application.html");
		public static final String APPLICATIONS_LIST = String.format(APPLICATION_CONTEXT, "applications-list.html");
	}
	
	public ApplicationController(Configuration configuration) {
		super(ApplicationController.class);
		configureRoutes(configuration);
	}
	
	private void configureRoutes(Configuration configuration) {
		get(Path.Route.APPLICATION_CONNECTOR_SELECT, (request, response) -> selectSalesforceConnector(configuration, request, response));
		get(Path.Route.APPLICATION_EDIT, (request, response) -> editApplication(configuration, request, response));
		get(Path.Route.APPLICATION_NEW, (request, response) -> newApplication(configuration, request, response));
		get(Path.Route.APPLICATION_VIEW, (request, response) -> viewApplication(configuration, request, response));
		get(Path.Route.APPLICATION_LIST, (request, response) -> getApplications(configuration, request, response));
		delete(Path.Route.APPLICATION_DELETE, (request, response) -> deleteApplication(configuration, request, response));
		post(Path.Route.APPLICATION_CREATE, (request, response) -> createApplication(configuration, request, response));
		post(Path.Route.APPLICATION_UPDATE, (request, response) -> updateApplication(configuration, request, response));
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * selectSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String selectSalesforceConnector(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.execute();
		
		List<SalesforceConnector> salesforceConnectors = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		} else {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnectorsList", salesforceConnectors);
		
		return render(configuration, request, response, model, Template.APPLICATION_CONNECTOR_SELECT);
	}

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * newApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String newApplication(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.queryParams("id");
		
		SalesforceConnector salesforceConnector = new NowellpointClient(token)
				.salesforceConnector()
				.get(id);
		
		Map<String, Object> model = getModel();
		model.put("mode", "new");
    	model.put("salesforceConnector", salesforceConnector);
		
		return render(configuration, request, response, model, Template.APPLICATION_EDIT);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * viewApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String viewApplication(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
				.path(id)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Application application = httpResponse.getEntity(Application.class);
		
		Map<String, Object> model = getModel();
		model.put("application", application);
		model.put("successMessage", request.cookie("successMessage"));
		
		return render(configuration, request, response, model, Template.APPLICATION);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String editApplication(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String view = request.queryParams("view");
		
		Token token = getToken(request);
		
		Application application = new NowellpointClient(token)
				.application()
				.getApplication(id);
		
		Map<String, Object> model = getModel();
		model.put("application", application);
		model.put("mode", "edit");
		
		if (view != null && view.equals("1")) {
			model.put("cancel", Path.Route.APPLICATION_LIST);
		} else {
			model.put("cancel", Path.Route.APPLICATION_VIEW.replace(":id", id));
		}
		
		return render(configuration, request, response, model, Template.APPLICATION_EDIT);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getApplications
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String getApplications(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
				.execute();
		
		List<Application> applications = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			applications = httpResponse.getEntityList(Application.class);
		} else {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Map<String, Object> model = getModel();
		model.put("applicationList", applications);
		
		return render(configuration, request, response, model, Template.APPLICATIONS_LIST);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * createApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String createApplication(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("applications")
				.parameter("name", request.queryParams("name"))
				.parameter("description", request.queryParams("description"))
				.parameter("importSandboxes", request.queryParams("importSandboxes") != null ? "true" : "false")
				.parameter("importServices", request.queryParams("importServices") != null ? "true" : "false")
				.parameter("connectorId", request.queryParams("connectorId"))
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Application application = httpResponse.getEntity(Application.class);
		
		response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", application.getId()));
		
		return "";
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String updateApplication(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("applications")
				.path(id)
				.parameter("name", request.queryParams("name"))
				.parameter("description", request.queryParams("description"))
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Application application = httpResponse.getEntity(Application.class);
		
		response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", application.getId()));
		
		return "";
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * deleteApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String deleteApplication(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		Token token = request.attribute("token");
		
		NowellpointClient client = new NowellpointClient(token);
		client.application().deleteApplication(id);
		
		return "";	
	}
}
package com.nowellpoint.www.app.view;

import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Application;
import com.nowellpoint.www.app.model.SalesforceConnector;
import com.nowellpoint.www.app.service.GetSalesforceConnectorRequest;
import com.nowellpoint.www.app.service.SalesforceConnectorService;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApplicationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationController.class);
	
	private static final SalesforceConnectorService salesforceConnectorService = new SalesforceConnectorService();
	
	public ApplicationController(Configuration cfg) {
		super(ApplicationController.class, cfg);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * selectSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route selectSalesforceConnector = (Request request, Response response) -> {
		
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
		
		return render(request, model, Path.Template.APPLICATION_CONNECTOR_SELECT);
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * newApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route newApplication = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.queryParams("id");
		
		GetSalesforceConnectorRequest getSalesforceConnectorRequest = new GetSalesforceConnectorRequest()
				.withAccessToken(token.getAccessToken())
				.withId(id);
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.getSalesforceConnector(getSalesforceConnectorRequest);
		
		Map<String, Object> model = getModel();
		model.put("mode", "new");
    	model.put("salesforceConnector", salesforceConnector);
		
		return render(request, model, Path.Template.APPLICATION_EDIT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route getApplication = (Request request, Response response) -> {
		
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
		
		return render(request, model, Path.Template.APPLICATION);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route editApplication = (Request request, Response response) -> {
		
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
		model.put("mode", "edit");
		
		return render(request, model, Path.Template.APPLICATION_EDIT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getSalesforceConnectors
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route getApplications = (Request request, Response response) -> {
		
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
		
		return render(request, model, Path.Template.APPLICATIONS_LIST);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * createApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route createApplication = (Request request, Response response) -> {
		
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
		
		response.redirect(Path.Route.APPLICATION.replace(":id", application.getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route updateApplication = (Request request, Response response) -> {
		
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
		
		response.redirect(Path.Route.APPLICATION.replace(":id", application.getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * deleteApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route deleteApplication = (Request request, Response response) -> {
		
		String applicationId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("application")
				.path(applicationId)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		return "";	
	};
}
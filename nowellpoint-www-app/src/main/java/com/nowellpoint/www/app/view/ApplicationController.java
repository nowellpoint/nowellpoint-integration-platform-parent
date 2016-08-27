package com.nowellpoint.www.app.view;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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
	 * newApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route newApplication = (Request request, Response response) -> {
		
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
		model.put("mode", "select");
    	model.put("salesforceConnectorsList", salesforceConnectors);
		
		return render(request, model, Path.Template.APPLICATION_CONNECTOR_SELECT);
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * addSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route importSalesforceConnector = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.queryParams("id");
		
		GetSalesforceConnectorRequest getSalesforceConnectorRequest = new GetSalesforceConnectorRequest()
				.withAccessToken(token.getAccessToken())
				.withId(id);
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.getSalesforceConnector(getSalesforceConnectorRequest);
		
		Map<String, Object> model = getModel();
		model.put("mode", "import");
    	model.put("salesforceConnector", salesforceConnector);
		
		return render(request, model, Path.Template.APPLICATION_CONNECTOR_SELECT);
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
		
		System.out.println(application.getId());
		
		Map<String, Object> model = getModel();
		model.put("application", application);
		
		return render(request, model, Path.Template.APPLICATION);
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
package com.nowellpoint.www.app.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Application;
import com.nowellpoint.www.app.model.SalesforceConnector;
import com.nowellpoint.www.app.model.ServiceProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApplicationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationController.class);
	
	public ApplicationController(Configuration cfg) {
		super(ApplicationController.class, cfg);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	public Route newApplication = (Request request, Response response) -> {
		
		Token token = request.attribute("token");
		
		String serviceProviderId = request.params(":id");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.path(serviceProviderId)
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		ServiceProvider provider = httpResponse.getEntity(ServiceProvider.class);
		
		httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("connectors")
    			.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<SalesforceConnector> salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		
		Account account = request.attribute("account");
		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("account", account);
    	model.put("serviceProvider", provider);
    	model.put("salesforceConnectorsList", salesforceConnectors);
		model.put("application", new Application());
		
		return render(request, model, Path.Template.APPLICATION);
	};
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	public Route getApplication = (Request request, Response response) -> {
		
		String applicationId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("application")
				.path(applicationId)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Application application = httpResponse.getEntity(Application.class);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("application", application);
		
		return render(request, model, Path.Template.APPLICATION_EDIT);
	};
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
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
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	public Route saveApplication = (Request request, Response response) -> {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = null;
		
		if (request.queryParams("id").trim().isEmpty()) {
			
			String body;
			try {
				body = new StringBuilder()
						.append("serviceProviderId=")
						.append(request.queryParams("serviceProviderId"))
						.append("&name=")
						.append(URLEncoder.encode(request.queryParams("name"), "UTF-8"))
						.append("&connectorId=")
						.append(request.queryParams("connectorId"))
						.toString();
				
			} catch (UnsupportedEncodingException e) {
				throw new BadRequestException(e);
			}
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("application")
					.body(body)
					.execute();
			
		} else {
			
			String body;
			try {
				body = new StringBuilder()
						.append("name=")
						.append(URLEncoder.encode(request.queryParams("name"), "UTF-8"))
						.toString();
				
			} catch (UnsupportedEncodingException e) {
				throw new BadRequestException(e);
			}
			
			httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("application")
					.path(request.queryParams("id"))
					.body(body)
					.execute();
			
		}
		
		if (httpResponse.getStatusCode() != Status.OK && httpResponse.getStatusCode() != Status.CREATED) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Application application = httpResponse.getEntity(Application.class);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("application", application);
		
		return render(request, model, "secure/");
	};
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
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
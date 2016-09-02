package com.nowellpoint.www.app.view;

import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.ServiceProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

public class ServiceProviderController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(ServiceProviderController.class.getName());
	
	public ServiceProviderController(Configuration cfg) {   
		super(ServiceProviderController.class, cfg);
	}
	
	public void configureRoutes(Configuration cfg) {
		
	}
	
	public Route getServiceProviders = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.queryParameter("localeSidKey", "en_US")
				.queryParameter("languageLocaleKey", "en_US")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		List<ServiceProvider> providers = httpResponse.getEntityList(ServiceProvider.class);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("serviceProviders", providers);
    	
		return new ModelAndView(model, Path.Template.SERVICE_CATALOG);
	};
	
	public Route getServiceProvider = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		String id = request.params(":id");
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.path(id)
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		ServiceProvider provider = httpResponse.getEntity(ServiceProvider.class);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("serviceProvider", provider);
		
		return new ModelAndView(model, Path.Template.SALESFORCE_OUTBOUND_MESSAGE);
	};
	
	public Route deleteServiceProvider = (Request request, Response response) -> {
		
		String id = request.params("id"); 
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.path(id)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() != Status.NO_CONTENT.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		return "";
	};
}
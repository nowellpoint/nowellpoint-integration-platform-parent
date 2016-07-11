package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.ServiceProvider;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ServiceProviderController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(ServiceProviderController.class.getName());
	
	public ServiceProviderController(Configuration cfg) {   
		super(ServiceProviderController.class, cfg);
	}
	
	public void configureRoutes(Configuration cfg) {
		get("/app/providers", (request, response) -> getServiceProviders(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/providers/:id", (request, response) -> getServiceProvider(request, response), new FreeMarkerEngine(cfg));
        
        delete("/app/providers/:id", (request, response) -> deleteServiceProvider(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private ModelAndView getServiceProviders(Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
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
    	
		return new ModelAndView(model, "secure/service-catalog.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private ModelAndView getServiceProvider(Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String id = request.params(":id");
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
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
		
		return new ModelAndView(model, "secure/salesforce-outbound-messages.html");
	}
	
	private String deleteServiceProvider(Request request, Response response) throws IOException {
		
		String id = request.params("id"); 
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.path(id)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() != Status.NO_CONTENT.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		return "";
	}
}
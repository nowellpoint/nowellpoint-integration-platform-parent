package com.nowellpoint.www.app.view;

import java.util.List;
import java.util.Map;

import javax.ws.rs.NotAuthorizedException;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.Property;
import com.nowellpoint.client.model.idp.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class AdministrationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AdministrationController.class);
	
	public AdministrationController(Configuration cfg) {
		super(AdministrationController.class, cfg);
	}
	
	public void configureRoutes(Configuration cfg) {
		
	
	}
	
	public Route showAdministrationHome = (Request request, Response response) -> {
		
		AccountProfile account = getAccount(request);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		
		return render(request, model, Path.Template.ADMINISTRATION_HOME);
		
	};
	
	public Route showManageCache = (Request request, Response response) -> {
		
		AccountProfile account = getAccount(request);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		
		return render(request, model, Path.Template.CACHE_MANAGER);
		
	};
	
	public Route purgeCache = (Request request, Response response) -> {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("cache")
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		AccountProfile account = getAccount(request);
		Map<String, Object> model = getModel();
		model.put("account", account);
		
		return render(request, model, Path.Template.CACHE_MANAGER);
	};
	
	public Route showManageProperties = (Request request, Response response) -> {
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("properties")
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() == Status.NOT_AUTHORIZED) {
			throw new NotAuthorizedException(httpResponse.getAsString());
		}
		
		List<Property> properties = httpResponse.getEntityList(Property.class);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("propertyList", properties);
		
		return render(request, model, Path.Template.PROPERTY_MANAGER);
	};
}
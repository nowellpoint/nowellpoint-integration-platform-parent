package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Map;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class AdministrationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AdministrationController.class.getName());
	
	public static class Template {
		public static final String ADMINISTRATION_HOME = String.format(APPLICATION_CONTEXT, "administration-home.html");
		public static final String CACHE_MANAGER = String.format(APPLICATION_CONTEXT, "cache.html");
	}
	
	public AdministrationController() {
		super(AdministrationController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.ADMINISTRATION, (request, response) -> showAdministrationHome(configuration, request, response));	
        get(Path.Route.ADMINISTRATION.concat("/cache"), (request, response) -> showManageCache(configuration, request, response));	
		get(Path.Route.ADMINISTRATION.concat("/cache/purge"), (request, response) -> purgeCache(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String showAdministrationHome(Configuration configuration, Request request, Response response) {
		
		Identity identity = getIdentity(request);
		
		Map<String, Object> model = getModel();
		model.put("account", identity);
		
		return render(configuration, request, response, model, Template.ADMINISTRATION_HOME);
		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String showManageCache(Configuration configuration, Request request, Response response) {
		
		Identity identity = getIdentity(request);
		
		Map<String, Object> model = getModel();
		model.put("account", identity);
		
		return render(configuration, request, response, model, Template.CACHE_MANAGER);
		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String purgeCache(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("cache")
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			LOGGER.error(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
		model.put("account", identity);
		
		return render(configuration, request, response, model, Template.CACHE_MANAGER);
	};
}
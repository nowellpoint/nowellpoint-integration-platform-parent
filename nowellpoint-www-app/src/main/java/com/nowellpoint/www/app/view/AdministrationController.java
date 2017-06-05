package com.nowellpoint.www.app.view;

import com.nowellpoint.client.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class AdministrationController extends AbstractStaticController {
	
	private static final Logger LOGGER = Logger.getLogger(AdministrationController.class.getName());
	
	public static class Template {
		public static final String ADMINISTRATION_HOME = String.format(APPLICATION_CONTEXT, "administration-home.html");
		public static final String CACHE_MANAGER = String.format(APPLICATION_CONTEXT, "cache.html");
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String serveAdminHomePage(Configuration configuration, Request request, Response response) {
		return render(AdministrationController.class, configuration, request, response, getModel(), Template.ADMINISTRATION_HOME);
		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String showManageCache(Configuration configuration, Request request, Response response) {
		return render(AdministrationController.class, configuration, request, response, getModel(), Template.CACHE_MANAGER);
		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String purgeCache(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path("cache")
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			LOGGER.error(httpResponse.getAsString());
		}
		
		return render(AdministrationController.class, configuration, request, response, getModel(), Template.CACHE_MANAGER);
	};
}
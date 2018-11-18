package com.nowellpoint.console.view;

import static spark.Spark.get;

import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.http.HttpRequestException;

import spark.Request;
import spark.Response;

public class SalesforceOauthController extends BaseController {
	
	public static void configureRoutes() {
		get(Path.Route.SALESFORCE_OAUTH_CALLBACK, (request, response) 
				-> oauthCallback(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String oauthCallback(Request request, Response response) {

        String organizationId = request.queryParams("state");
        String authorizationCode = request.queryParams("code");
        
        try {
			ServiceClient.getInstance().organization().update(organizationId, authorizationCode);
        } catch (HttpRequestException e) {
        	e.printStackTrace();
        }
		
		response.redirect(Path.Route.START);
	   
		return "";
	}
}
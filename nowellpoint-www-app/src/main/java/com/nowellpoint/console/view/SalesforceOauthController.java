package com.nowellpoint.console.view;

import static spark.Spark.get;

import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;

import java.util.logging.Logger;
import spark.Request;
import spark.Response;

public class SalesforceOauthController extends BaseController {
	
	private static final Logger logger = Logger.getLogger(SalesforceOauthController.class.getName());
	
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
        
        Organization organization = ServiceClient.getInstance()
        		.organization()
        		.update(organizationId, authorizationCode);
        
        logger.info(String.format("Organization %s has been setup for Salesforce access", organization.getName()));
		
		response.redirect(Path.Route.START);
	   
		return "";
	}
}
package com.nowellpoint.console.util;

import static spark.Spark.get;

import javax.ws.rs.NotFoundException;

import org.eclipse.jetty.http.HttpStatus;

import com.nowellpoint.console.api.IdentityResource;
import com.nowellpoint.console.api.OrganizationResource;
import com.nowellpoint.console.view.AdministrationController;
import com.nowellpoint.console.view.IndexController;
import com.nowellpoint.console.view.NotificationController;
import com.nowellpoint.console.view.OrganizationController;
import com.nowellpoint.console.view.SignUpController;
import com.nowellpoint.console.view.StartController;
import com.nowellpoint.console.view.UserProfileController;
import com.nowellpoint.console.view.SalesforceOauthController;

import spark.Request;
import spark.Response;

public class Routes {
	
	public static void configureRoutes() {
		
		AdministrationController.configureRoutes();
		IndexController.configureRoutes();
		SignUpController.configureRoutes();
		StartController.configureRoutes();
		UserProfileController.configureRoutes();
		OrganizationController.configureRoutes();
		NotificationController.configureRoutes();
		IdentityResource.configureRoutes();
		OrganizationResource.configureRoutes();
		SalesforceOauthController.configureRoutes();
		
		get(Path.Route.HEALTH_CHECK, (request, response) 
				-> healthCheck(request, response));
		
		get("*", (request, response) -> {
			throw new NotFoundException();
		});
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String healthCheck(Request request, Response response) {
		response.status(HttpStatus.OK_200);
		return "";
	}
}
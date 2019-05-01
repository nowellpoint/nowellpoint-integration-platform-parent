package com.nowellpoint.console.util;

import static spark.Spark.get;

import javax.ws.rs.NotFoundException;

import org.eclipse.jetty.http.HttpStatus;

import com.nowellpoint.console.api.IdentityResource;
import com.nowellpoint.console.api.OrganizationResource;
import com.nowellpoint.console.view.AdministrationController;
import com.nowellpoint.console.view.AuthenticationController;
import com.nowellpoint.console.view.IndexController;
import com.nowellpoint.console.view.NotificationController;
import com.nowellpoint.console.view.OrganizationController;
import com.nowellpoint.console.view.SignUpController;
import com.nowellpoint.console.view.StartController;
import com.nowellpoint.console.view.EventStreamsController;
import com.nowellpoint.console.view.UserProfileController;
import com.nowellpoint.console.view.SalesforceOauthController;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class Routes {
	
	public static void configureRoutes(Configuration configuration) {
		
//		if (! Optional.ofNullable(configuration.getSharedVariable("countryList")).isPresent()) {
//			try {
//				configuration.setSharedVariable("countryList", loadCountries(configuration.getLocale()));
//			} catch (TemplateModelException e) {
//				e.printStackTrace();
//				halt();
//			}
//		}
//		
//		if (! Optional.ofNullable(configuration.getSharedVariable("planList")).isPresent()) {
//			try {
//				configuration.setSharedVariable("planList", loadPlans(configuration.getLocale()));
//			} catch (TemplateModelException e) {
//				e.printStackTrace();
//				halt();
//			}
//		}
		
		AdministrationController.configureRoutes();
		IndexController.configureRoutes();
		SignUpController.configureRoutes();
		AuthenticationController.configureRoutes();
		StartController.configureRoutes();
		UserProfileController.configureRoutes();
		OrganizationController.configureRoutes();
		NotificationController.configureRoutes();
		EventStreamsController.configureRoutes();
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
	
	/**
	 * 
	 * @param locale
	 * @return List of IsoCountries
	 */
	
//	private static List<IsoCountry> loadCountries(Locale locale) {				
//		return ContentService.getInstance()
//				.getCountries()
//				.getItems();
//	}
	
	/**
	 * 
	 * @return List of Plans
	 */
	
//	private static List<Plan> loadPlans(Locale locale) {				
//		return ContentService.getInstance()
//				.getPlans()
//				.getItems();
//	}
}
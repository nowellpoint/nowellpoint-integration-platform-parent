package com.nowellpoint.console.util;

import static spark.Spark.halt;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.nowellpoint.console.api.IdentityResource;
import com.nowellpoint.console.api.OrganizationResource;
import com.nowellpoint.console.view.AdministrationController;
import com.nowellpoint.console.view.AuthenticationController;
import com.nowellpoint.console.view.IndexController;
import com.nowellpoint.console.view.NotificationController;
import com.nowellpoint.console.view.OrganizationController;
import com.nowellpoint.console.view.SignUpController;
import com.nowellpoint.console.view.StartController;
import com.nowellpoint.console.view.StreamingEventsController;
import com.nowellpoint.console.view.UserProfileController;
import com.nowellpoint.console.view.SalesforceOauthController;
import com.nowellpoint.content.model.IsoCountry;
import com.nowellpoint.content.model.Plan;
import com.nowellpoint.content.service.ContentService;

import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;

public class Routes {
	
	public static void configureRoutes(Configuration configuration) {
		
		if (! Optional.ofNullable(configuration.getSharedVariable("countryList")).isPresent()) {
			try {
				configuration.setSharedVariable("countryList", loadCountries(configuration.getLocale()));
			} catch (TemplateModelException e) {
				e.printStackTrace();
				halt();
			}
		}
		
		if (! Optional.ofNullable(configuration.getSharedVariable("planList")).isPresent()) {
			try {
				configuration.setSharedVariable("planList", loadPlans(configuration.getLocale()));
			} catch (TemplateModelException e) {
				e.printStackTrace();
				halt();
			}
		}
		
		AdministrationController.configureRoutes();
		IndexController.configureRoutes(configuration);
		SignUpController.configureRoutes();
		AuthenticationController.configureRoutes(configuration);
		StartController.configureRoutes();
		UserProfileController.configureRoutes(configuration);
		OrganizationController.configureRoutes();
		NotificationController.configureRoutes(configuration);
		StreamingEventsController.configureRoutes();
		IdentityResource.configureRoutes();
		OrganizationResource.configureRoutes();
		SalesforceOauthController.configureRoutes();
	}
	
	/**
	 * 
	 * @param locale
	 * @return List of IsoCountries
	 */
	
	private static List<IsoCountry> loadCountries(Locale locale) {				
		return ContentService.getInstance()
				.getCountries()
				.getItems();
	}
	
	/**
	 * 
	 * @return List of Plans
	 */
	
	private static List<Plan> loadPlans(Locale locale) {				
		return ContentService.getInstance()
				.getPlans()
				.getItems();
	}
}
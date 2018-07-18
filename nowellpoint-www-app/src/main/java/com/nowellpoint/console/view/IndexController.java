package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import com.nowellpoint.console.model.LeadRequest;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.service.LeadService;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class IndexController {
	
	public static void configureRoutes(Configuration configuration) {
		
		get(Path.Route.INDEX, (request, response) 
				-> serveIndexPage(configuration, request, response));
		
		get(Path.Route.CONTACT, (request, response) 
				-> serveContactPage(configuration, request, response));
		
		post(Path.Route.CONTACT, (request, response) 
				-> contact(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String serveIndexPage(Configuration configuration, Request request, Response response) {
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(IndexController.class)
				.request(request)
				.templateName(Templates.INDEX)
				.build();
    	
    	return template.render();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String serveContactPage(Configuration configuration, Request request, Response response) {
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(IndexController.class)
				.request(request)
				.templateName(Templates.CONTACT)
				.build();
    	
    	return template.render();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String contact(Configuration configuration, Request request, Response response) {
		
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String email = request.queryParams("email");
		String message = request.queryParams("message");
		
    	LeadRequest leadRequest = LeadRequest.builder()
    			.email(email)
    			.firstName(firstName)
    			.lastName(lastName)
    			.message(message)
    			.build();
    	
    	LeadService leadService = new LeadService();
    	
    	leadService.createLead(leadRequest);
    	
    	return MessageProvider.getMessage(configuration.getLocale(), "contact.confirmation.message");
	};
	
	
}
package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nowellpoint.console.model.Lead;
import com.nowellpoint.console.model.LeadRequest;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.content.model.IsoCountry;
import com.nowellpoint.content.model.Plan;
import com.nowellpoint.content.service.ContentService;

import spark.Request;
import spark.Response;

public class IndexController extends BaseController {
	
	public static void configureRoutes() {
		
		get(Path.Route.INDEX, (request, response) 
				-> serveIndexPage(request, response));
		
		get(Path.Route.CONTACT, (request, response) 
				-> serveContactPage(request, response));
		
		post(Path.Route.CONTACT, (request, response) 
				-> contact(request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String serveIndexPage(Request request, Response response) {
		
		List<Plan> plans = ContentService.getInstance()
				.getPlans()
				.getItems();
		
		Map<String, Object> model = getModel();
		model.put("planList", plans);
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(IndexController.class)
				.model(model)
				.templateName(Templates.INDEX)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String serveContactPage(Request request, Response response) {
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(IndexController.class)
				.model(getModel())
				.templateName(Templates.CONTACT)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String contact(Request request, Response response) {
		
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String email = request.queryParams("email");
		String message = request.queryParams("message");
		
    	LeadRequest leadRequest = LeadRequest.builder()
    			.email(email)
    			.firstName(firstName)
    			.lastName(lastName)
    			.locale(Locale.getDefault())
    			.message(message)
    			.build();
    	
    	Lead lead = ServiceClient.getInstance()
    			.lead()
    			.create(leadRequest);
    	
    	Map<String,Object> model = getModel();
    	model.put("lead", lead);
    	
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(IndexController.class)
				.model(model)
				.templateName(Templates.CONTACT)
				.build();
		
		return processTemplate(templateProcessRequest);
	};
	
	/**
	 * 
	 * @param locale
	 * @return List of IsoCountries
	 */
	
	private static List<IsoCountry> loadCountries(Locale locale) {	
		System.out.println("countries");
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
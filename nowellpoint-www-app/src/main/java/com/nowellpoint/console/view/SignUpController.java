package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SignUpController {
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.FREE_ACCOUNT, (request, response) 
				-> freeAccount(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String freeAccount(Configuration configuration, Request request, Response response) {
		
		Plan plan = ServiceClient.getInstance()
				.plan()
				.getByCode("FREE");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("plan", plan);
		model.put("countryCode", configuration.getLocale().getCountry());
		model.put("action", "signUp");
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.model(model)
				.templateName(Templates.SIGN_UP)
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
	
	private static String signUp(Configuration configuration, Request request, Response response) {

		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String email = request.queryParams("email");
		String password = request.queryParams("password");
		String phone = request.queryParams("phone");
		String confirmPassword = request.queryParams("confirmPassword");
		String countryCode = request.queryParams("countryCode");
		String planId = request.queryParams("planId");
		String domain = request.queryParams("domain");

		

		if ("XX".equalsIgnoreCase(countryCode)) {

			
		}
		
		return null;
		
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String provision(Configuration configuration, Request request, Response response) {
		
		return null;
		
	}
}
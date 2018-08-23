package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.exception.ConsoleException;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.SignUpRequest;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.RequestAttributes;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SignUpController {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.FREE_ACCOUNT, (request, response) 
				-> freeAccount(configuration, request, response));
		
		post(Path.Route.SIGN_UP, (request, response) 
				-> signUp(configuration, request, response));
		
		post(Path.Route.RESEND, (request, response) 
				-> resend(configuration, request, response));
		
		get(Path.Route.ACTIVATE_ACCOUNT, (request, response)
				-> showActivateAccount(configuration, request, response));
		
		post(Path.Route.ACTIVATE_ACCOUNT, (request, response) 
				-> activateAccount(configuration, request, response));
		
		get(Path.Route.SECURE_ACCOUNT, (request, response)
				-> showSecureAccount(configuration, request, response));
		
		post(Path.Route.SECURE_ACCOUNT, (request, response) 
				-> secure(configuration, request, response));
		
		get(Path.Route.SALESFORCE_OAUTH, (request, response)
				-> showSalesforceOauth(configuration, request, response));
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
				.getFreePlan();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("plan", plan);
		
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
		String planId = request.queryParams("planId");
    	
    	try {
    		
    		SignUpRequest signUpRequest = SignUpRequest.builder()
        			.email(email)
        			.firstName(firstName)
        			.lastName(lastName)
        			.planId(planId)
        			.build();
    		
    		Identity identity = ServiceClient.getInstance()
        			.console()
        			.signUp(signUpRequest);
			
			response.redirect(Path.Route.ACTIVATE_ACCOUNT.replace(":id", identity.getId()));
			
			return "";
			
    	} catch (ConsoleException e) {
    		
    		LOGGER.severe(e.getMessage());
    		
    		Plan plan = ServiceClient.getInstance()
    				.plan()
    				.getFreePlan();
    		
    		Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", e.getMessage());
			model.put("plan", plan);
	    	
	    	Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(SignUpController.class)
					.request(request)
					.model(model)
					.templateName(Templates.SIGN_UP)
					.build();
			
			return template.render();
    	}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showActivateAccount(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
    	
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.model(model)
				.request(request)
				.templateName(Templates.ACTIVATE_ACCOUNT)
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
	
	private static String activateAccount(Configuration configuration, Request request, Response response) {
		
		String activationToken = request.queryParams("activationToken");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.activate(activationToken);
		
		response.redirect(Path.Route.SECURE_ACCOUNT.replace(":id", identity.getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String resend(Configuration configuration, Request request, Response response) {
		
		String id = request.queryParams("id");
		
		ServiceClient.getInstance().identity().resendActivationEmail(id);
		
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.templateName(Templates.ACTIVATE_ACCOUNT)
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
	
	private static String showSecureAccount(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
    	
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.model(model)
				.request(request)
				.templateName(Templates.SECURE_ACCOUNT)
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
	
	private static String secure(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String password = request.queryParams("password");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.setPassword(id, password);
		
		try {
			
			Token token = ServiceClient.getInstance()
					.console()
					.authenticate(identity.getUsername(), password);
			
			Long expiresIn = token.getExpiresIn();
			
			try {			
				response.cookie("/", RequestAttributes.AUTH_TOKEN, new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true, true);
			} catch (IOException e) {
				throw new InternalServerErrorException(e);
			}
			
			response.redirect(Path.Route.SALESFORCE_OAUTH.replace(":id", identity.getId()));
			
		} catch (ConsoleException e) {
			throw new InternalServerErrorException(e);
		}
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showSalesforceOauth(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
		
    	return Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.templateName(Templates.SALESFORCE_OAUTH)
				.build()
				.toHtml();
	}
}
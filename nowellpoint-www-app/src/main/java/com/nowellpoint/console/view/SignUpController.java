package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.oauth.model.OAuthClientException;
import com.nowellpoint.www.app.util.Path;
import com.okta.sdk.resource.ResourceException;

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
				.getByCode("FREE");
		
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
		String password = UUID.randomUUID().toString(); //request.queryParams("password");

		IdentityRequest identityRequest = IdentityRequest.builder()
    			.email(email)
    			.firstName(firstName)
    			.lastName(lastName)
    			.password(password)
    			.build();
    	
    	try {
    		
    		Identity identity = ServiceClient.getInstance()
        			.identity()
        			.create(identityRequest);
			
			response.redirect(Path.Route.ACTIVATE_ACCOUNT.replace(":id", identity.getId()));
			
			return "";
			
    	} catch (OAuthClientException e) {
    		
    		LOGGER.severe(e.getErrorDescription());
    		
    		Plan plan = ServiceClient.getInstance()
    				.plan()
    				.getByCode("FREE");
    		
    		Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", e.getErrorDescription());
			model.put("plan", plan);
	    	
	    	Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(SignUpController.class)
					.request(request)
					.model(model)
					.templateName(Templates.SIGN_UP)
					.build();
			
			return template.render();
			
    	} catch (ResourceException e) {
    		
    		LOGGER.severe(e.getMessage());
    		
    		Plan plan = ServiceClient.getInstance()
    				.plan()
    				.getByCode("FREE");
    		
    		Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", e.getOktaError().getMessage());
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
	
	private static String activateAccount(Configuration configuration, Request request, Response response) {
		
		String activationToken = request.queryParams("activationToken");
		
		ServiceClient.getInstance().identity().activate(activationToken);
		
		response.redirect(Path.Route.SALESFORCE_OAUTH);
		
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
		
		String id = request.params("id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		Map<String, Object> model = new HashMap<>();
		model.put("identity", identity);
    	
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.model(model)
				.request(request)
				.templateName(Templates.SECURE_ACCOUNT.replace(":id", identity.getId()))
				.build();
		
		return template.render();
	}
	
	private static String secure(Configuration configuration, Request request, Response response) {
		String id = request.params("id");
		
		return null;
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showSalesforceOauth(Configuration configuration, Request request, Response response) {
    	return Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.templateName(Templates.SALESFORCE_OAUTH)
				.build()
				.toHtml();
	}
}
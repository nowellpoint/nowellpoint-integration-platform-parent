package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.model.SignUpRequest;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.RequestAttributes;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.util.SecretsManager;
import com.okta.sdk.resource.ResourceException;

import spark.Request;
import spark.Response;

public class SignUpController extends BaseController {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
	
	public static void configureRoutes() {
		
		get(Path.Route.FREE_ACCOUNT, (request, response) 
				-> showSignUp(request, response));
		
		post(Path.Route.SIGN_UP, (request, response) 
				-> signUp(request, response));
		
		get(Path.Route.ACCOUNT_ACTIVATION_RESEND, (request, response) 
				-> resendAccountActivation(request, response));
		
		get(Path.Route.ACCOUNT_ACTIVATE, (request, response)
				-> showActivateAccount(request, response));
		
		post(Path.Route.ACCOUNT_ACTIVATE, (request, response) 
				-> activateAccount(request, response));
		
		get(Path.Route.ACCOUNT_SECURE, (request, response)
				-> showSecureAccount(request, response));
		
		post(Path.Route.ACCOUNT_SECURE, (request, response) 
				-> secureAccount(request, response));
		
		get(Path.Route.ACCOUNT_CONNECT_USER, (request, response)
				-> showConnectUser(request, response));
		
		get(Path.Route.ACCOUNT_CONNECT_USER_ADD, (request, response)
				-> connectUser(request, response));
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showSignUp(Request request, Response response) {
		
		Plan plan = ServiceClient.getInstance()
				.plan()
				.getFreePlan();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("plan", plan);
		model.put("ACCOUNT_SIGNUP_URI", Path.Route.SIGN_UP);
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(SignUpController.class)
				.model(model)
				.templateName(Templates.SIGN_UP)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String signUp(Request request, Response response) {

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
			
			response.redirect(Path.Route.ACCOUNT_ACTIVATE.replace(":id", identity.getId()));
			
			return "";
			
    	} catch (ResourceException e) {
    		
    		LOGGER.severe(e.getMessage());
    		
    		Plan plan = ServiceClient.getInstance()
    				.plan()
    				.getFreePlan();
    		
    		Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", e.getMessage());
			model.put("plan", plan);
			
			ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
					.controllerClass(SignUpController.class)
					.model(model)
					.templateName(Templates.SIGN_UP)
					.build();
			
			return processTemplate(templateProcessRequest);
    	}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showActivateAccount(Request request, Response response) {
		
		String id = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
		model.put("ACCOUNT_ACTIVATE_URI", Path.Route.ACCOUNT_ACTIVATE.replace(":id", identity.getId()));
		model.put("ACCOUNT_ACTIVATION_RESEND_URI", Path.Route.ACCOUNT_ACTIVATION_RESEND.replace(":id", identity.getId()));
		
		ProcessTemplateRequest processTemplateRequest = ProcessTemplateRequest.builder()
				.controllerClass(SignUpController.class)
				.model(model)
				.templateName(Templates.ACTIVATE_ACCOUNT)
				.build();
		
		return processTemplate(processTemplateRequest);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String activateAccount(Request request, Response response) {
		
		String activationToken = request.queryParams("activationToken");
		
		try {
			
			Identity identity = ServiceClient.getInstance()
					.identity()
					.activate(activationToken);
			
			response.redirect(Path.Route.ACCOUNT_SECURE.replace(":id", identity.getId()));
			
			return "";
		
		} catch (ResourceException e) {
    		
    		LOGGER.severe(e.getMessage());
    		
    		String id = request.params(":id");
    		
    		Identity identity = ServiceClient.getInstance()
    				.identity()
    				.get(id);
    		
    		Map<String, Object> model = new HashMap<>();
    		model.put("registration", identity);
    		model.put("ACCOUNT_ACTIVATE_URI", Path.Route.ACCOUNT_ACTIVATE.replace(":id", identity.getId()));
    		model.put("ACCOUNT_ACTIVATION_RESEND_URI", Path.Route.ACCOUNT_ACTIVATION_RESEND.replace(":id", identity.getId()));
    		
    		ProcessTemplateRequest processTemplateRequest = ProcessTemplateRequest.builder()
    				.controllerClass(SignUpController.class)
    				.model(model)
    				.templateName(Templates.ACTIVATE_ACCOUNT)
    				.build();
			
			return processTemplate(processTemplateRequest);
    	}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String resendAccountActivation(Request request, Response response) {
		
		String id = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.resendActivationEmail(id);
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
		model.put("ACCOUNT_ACTIVATE_URI", Path.Route.ACCOUNT_ACTIVATE.replace(":id", id));
		model.put("ACCOUNT_ACTIVATION_RESEND_URI", Path.Route.ACCOUNT_ACTIVATION_RESEND.replace(":id", id));
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(SignUpController.class)
				.model(model)
				.templateName(Templates.ACTIVATE_ACCOUNT)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showSecureAccount(Request request, Response response) {
		
		String id = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
		model.put("ACCOUNT_SECURE_URI", Path.Route.ACCOUNT_SECURE.replace(":id", id));
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(SignUpController.class)
				.model(model)
				.templateName(Templates.SECURE_ACCOUNT)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String secureAccount(Request request, Response response) {
		
		String id = request.params(":id");
		char[] password = request.queryParams("password").toCharArray();
		
		try {
			
			Identity identity = ServiceClient.getInstance()
					.identity()
					.setPassword(id, password);
			
			Token token = ServiceClient.getInstance()
					.console()
					.authenticate(identity.getUsername(), password);
			
			Long expiresIn = token.getExpiresIn();
			
			try {			
				response.cookie("/", RequestAttributes.AUTH_TOKEN, new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true, true);
			} catch (IOException e) {
				throw new InternalServerErrorException(e);
			}
			
			response.redirect(Path.Route.ACCOUNT_CONNECT_USER.replace(":id", identity.getId()));
			
			return "";
			
		} catch (ResourceException e) {
			
			LOGGER.severe(e.getMessage());
    		
			Identity identity = ServiceClient.getInstance()
					.identity()
					.get(id);
			
			Map<String, Object> model = new HashMap<>();
			model.put("registration", identity);
			model.put("ACCOUNT_SECURE_URI", Path.Route.ACCOUNT_SECURE.replace(":id", id));
			
			ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
					.controllerClass(SignUpController.class)
					.model(model)
					.templateName(Templates.SIGN_UP)
					.build();
			
			return processTemplate(templateProcessRequest);	
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showConnectUser(Request request, Response response) {
		
		String identityId = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(identityId);
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
		model.put("CONNECT_USER_URI", Path.Route.ACCOUNT_CONNECT_USER_ADD.replace(":id", identity.getId()));
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(SignUpController.class)
				.model(model)
				.templateName(Templates.CONNECT_USER)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String connectUser(Request request, Response response) {
		
		String identityId = request.params(":id");

		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(identityId);
		
		try {
			
			String authUrl = new StringBuilder("https://login.salesforce.com/services/oauth2/authorize")
					.append("?response_type=code")
					.append("&client_id=")
					.append(SecretsManager.getSalesforceClientId())
					.append("&redirect_uri=")
					.append(System.getProperty("salesforce.oauth.callback"))
					.append("&scope=")
					.append(URLEncoder.encode("refresh_token api", "UTF-8"))
					.append("&prompt=login")
					.append("&display=popup")
					.append("&state=")
					.append(identity.getOrganization().getId())
					.toString();
			
			response.redirect(authUrl);	
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "";
	}
}
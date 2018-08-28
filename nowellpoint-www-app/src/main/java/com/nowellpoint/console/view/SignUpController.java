package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.exception.ConsoleException;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.SignUpRequest;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.TemplateProcessRequest;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.RequestAttributes;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SignUpController {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.FREE_ACCOUNT, (request, response) 
				-> showSignUp(configuration, request, response));
		
		post(Path.Route.SIGN_UP, (request, response) 
				-> signUp(configuration, request, response));
		
		get(Path.Route.ACCOUNT_ACTIVATION_RESEND, (request, response) 
				-> resendAccountActivation(configuration, request, response));
		
		get(Path.Route.ACCOUNT_ACTIVATE, (request, response)
				-> showActivateAccount(configuration, request, response));
		
		post(Path.Route.ACCOUNT_ACTIVATE, (request, response) 
				-> activateAccount(configuration, request, response));
		
		get(Path.Route.ACCOUNT_SECURE, (request, response)
				-> showSecureAccount(configuration, request, response));
		
		post(Path.Route.ACCOUNT_SECURE, (request, response) 
				-> secureAccount(configuration, request, response));
		
		get(Path.Route.ACCOUNT_LINK, (request, response)
				-> showLinkAccount(configuration, request, response));
		
		post(Path.Route.ACCOUNT_LINK, (request, response)
				-> linkAccount(configuration, request, response));
		
		get(Path.Route.SALESFORCE_OAUTH_CALLBACK, (request, response) 
				-> oauthCallback(configuration, request, response));
		
		get(Path.Route.SALESFORCE_OAUTH_SUCCESS, (request, response) 
				-> oauthSuccess(configuration, request, response));

	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showSignUp(Configuration configuration, Request request, Response response) {
		
		Plan plan = ServiceClient.getInstance()
				.plan()
				.getFreePlan();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("plan", plan);
		model.put("ACCOUNT_SIGNUP_URI", Path.Route.SIGN_UP);
		
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
			
			response.redirect(Path.Route.ACCOUNT_ACTIVATE.replace(":id", identity.getId()));
			
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
		model.put("ACCOUNT_ACTIVATE_URI", Path.Route.ACCOUNT_ACTIVATE.replace(":id", id));
		model.put("ACCOUNT_ACTIVATION_RESEND_URI", Path.Route.ACCOUNT_ACTIVATION_RESEND.replace(":id", id));
    	
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
		
		response.redirect(Path.Route.ACCOUNT_SECURE.replace(":id", identity.getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String resendAccountActivation(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
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
		
		TemplateManager templateManager = new TemplateManager();
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
		model.put("ACCOUNT_SECURE_URI", Path.Route.ACCOUNT_SECURE.replace(":id", id));
		
		TemplateProcessRequest templateProcessRequest = TemplateProcessRequest.builder()
				.controllerClass(SignUpController.class)
				.model(model)
				.request(request)
				.templateName(Templates.SECURE_ACCOUNT)
				.build();
		
		return templateManager.processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String secureAccount(Configuration configuration, Request request, Response response) {
		
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
			
			response.redirect(Path.Route.ACCOUNT_LINK.replace(":id", identity.getId()));
			
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
	
	private static String showLinkAccount(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		String authUrl = new StringBuilder(System.getenv("SALESFORCE_AUTHORIZE_URI"))
				.append("?response_type=token")
				.append("&client_id=")
				.append(System.getenv("SALESFORCE_CLIENT_ID"))
				.append("&client_secret=")
				.append(System.getenv("SALESFORCE_CLIENT_SECRET"))
				.append("&redirect_uri=")
				.append(System.getenv("SALESFORCE_REDIRECT_URI"))
				.append("&scope=api")
				.append("&display=popup")
				.append("&state=")
				.append(identity.getId())
				.toString();
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
		model.put("SALESFORCE_AUTHORIZE_URI", authUrl);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.model(model)
				.request(request)
				.templateName(Templates.SALESFORCE_OAUTH)
				.build();
		
		return template.render();
	}
	
	private static String oauthCallback(Configuration configuration, Request request, Response response) {
		return Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.templateName(Templates.SALESFORCE_OAUTH_CALLBACK)
				.build()
				.toHtml();
	}
	
	private static String linkAccount(Configuration configuration, Request request, Response response) {
		String id = request.params(":id");
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode token = mapper.readTree(request.body());
			
			String accessToken = URLDecoder.decode(token.get("access_token").asText(), "UTF-8");
			
			HttpResponse identityResponse = RestResource.get(URLDecoder.decode(token.get("id").asText(), "UTF-8"))
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(accessToken)
					.queryParameter("version", "latest")
					.execute();
			
			JsonNode identityNode = mapper.readTree(identityResponse.getAsString());
			
			HttpResponse organizationResponse = RestResource.get(identityNode.get("urls").get("sobjects").asText())
					.bearerAuthorization(accessToken)
	     			.path("Organization")
	     			.path(identityNode.get("organization_id").asText())
	     			.queryParameter("fields", "Name,Division")
	     			.queryParameter("version", "latest")
	     			.execute();
			
			JsonNode organization = mapper.readTree(organizationResponse.getAsString());
			
			Identity identity = ServiceClient.getInstance()
					.identity()
					.get(id);
			
			ServiceClient.getInstance().organization().update(
					identity.getOrganization().getId(), 
					organization.get("Name").asText(), 
					organization.get("Id").asText());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	   
		return "";
	}
	
	private static String oauthSuccess(Configuration configuration, Request request, Response response) {
		String id = request.params(":id");
		
		return Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.templateName(Templates.SALESFORCE_OAUTH_SUCCESS)
				.build()
				.toHtml();
	}
}
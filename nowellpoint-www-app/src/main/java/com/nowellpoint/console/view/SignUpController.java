package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.exception.ConsoleException;
import com.nowellpoint.console.model.ConnectionRequest;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.model.SignUpRequest;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.RequestAttributes;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

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
		
		get(Path.Route.ACCOUNT_LINK, (request, response)
				-> showLinkAccount(request, response));
		
		post(Path.Route.ACCOUNT_LINK, (request, response)
				-> linkAccount(request, response));
		
		get(Path.Route.SALESFORCE_OAUTH_CALLBACK, (request, response) 
				-> oauthCallback(request, response));
		
		get(Path.Route.SALESFORCE_OAUTH_SUCCESS, (request, response) 
				-> oauthSuccess(request, response));

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
			
    	} catch (ConsoleException e) {
    		
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
		
		System.out.println(Path.Route.ACCOUNT_ACTIVATE.replace(":id", identity.getId()));
		
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
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.activate(activationToken);
		
		response.redirect(Path.Route.ACCOUNT_SECURE.replace(":id", identity.getId()));
		
		return "";
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
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String showLinkAccount(Request request, Response response) {
		
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
				.append(identity.getOrganization().getId())
				.toString();
		
		Map<String, Object> model = new HashMap<>();
		model.put("registration", identity);
		model.put("SALESFORCE_AUTHORIZE_URI", authUrl);
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(SignUpController.class)
				.model(model)
				.templateName(Templates.SALESFORCE_OAUTH)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String oauthCallback(Request request, Response response) {	

		System.out.println(request.queryString());
		
		String authorizationCode = request.queryParams("code");
		String state = request.queryParams("state");

		System.out.println(authorizationCode);

		String tokenUrl = new StringBuilder(System.getenv("SALESFORCE_AUTHORIZE_URI"))
				.append("&client_id=")
				.append(System.getenv("SALESFORCE_CLIENT_ID"))
				.append("&client_secret=")
				.append(System.getenv("SALESFORCE_CLIENT_SECRET"))
				.append("&redirect_uri=")
				.append(System.getenv("SALESFORCE_REDIRECT_URI"))
				.append("&grant_type=authorization_code")
				.append("&code=")
				.append(authorizationCode)
				.toString();

				System.out.println(tokenUrl);

		Map<String, Object> model = new HashMap<>();
		model.put("LINK_ACCOUNT_URI", Path.Route.ACCOUNT_LINK);
		model.put("LINK_ACCOUNT_SUCCESS_URI", Path.Route.SALESFORCE_OAUTH_SUCCESS);
		model.put("TOKEN_URL", tokenUrl);
		model.put("ORGANIZATION_ID", state);
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(SignUpController.class)
				.model(model)
				.templateName(Templates.SALESFORCE_OAUTH_CALLBACK)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String linkAccount(Request request, Response response) {
		
		String organizationId = request.params(":id");
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode tokenNode = mapper.readTree(request.body());
			
			String id = URLDecoder.decode(tokenNode.get("id").asText(), "UTF-8");
			String accessToken = URLDecoder.decode(tokenNode.get("access_token").asText(), "UTF-8");
			String instanceUrl = URLDecoder.decode(tokenNode.get("instance_url").asText(), "UTF-8");
			
			HttpResponse identityResponse = RestResource.get(id)
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
			
			JsonNode organizationNode = mapper.readTree(organizationResponse.getAsString());
			
			ConnectionRequest connectorRequest = ConnectionRequest.builder()
					.connectedUser(identityNode.get("username").asText())
					.domain(organizationNode.get("Id").asText())
					.encryptedToken(Base64.getEncoder().encodeToString(tokenNode.toString().getBytes()))
					.instanceUrl(instanceUrl)
					.name(organizationNode.get("Name").asText())
					.build();
			
			ServiceClient.getInstance().organization().update(
					organizationId, 
					connectorRequest);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	   
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String oauthSuccess(Request request, Response response) {
		String id = request.params(":id");
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(SignUpController.class)
				.templateName(Templates.SALESFORCE_OAUTH_SUCCESS)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
}
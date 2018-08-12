package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.IdentityRequest;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.RequestAttributes;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.oauth.model.OAuthClientException;
import com.nowellpoint.www.app.util.Path;
import com.okta.sdk.resource.ResourceException;

import freemarker.template.Configuration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
		String password = request.queryParams("password");

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
    		
    		Calendar expiration = Calendar.getInstance();
    		expiration.add(Calendar.MINUTE, 60);
    		
    		String jws = Jwts.builder()
    				.setHeaderParam("kid", "9999999999")
    				.setId(identity.getSubject())
    				.setIssuer("nowellpoint.com")
    				.setAudience("00000000000")
    				.setSubject(identity.getId())
    				.setExpiration(expiration.getTime())
    				.setIssuedAt(Date.from(Instant.now()))
    				.claim("scope", "temporary")
    				.signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8"))
    				.compact();

    		Token token = Token.builder()
    				.environmentUrl(request.host())
    				.id(identity.getId().toString())
    				.accessToken(jws)
    				.expiresIn(expiration.getTimeInMillis())
    				.tokenType("Bearer")
    				.build();
			
			Long expiresIn = token.getExpiresIn();
			
			try {			
				response.cookie("/", RequestAttributes.AUTH_TOKEN, new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true, true);
			} catch (IOException e) {
				throw new InternalServerErrorException(e);
			}
			
			response.redirect(Path.Route.ACTIVATE_ACCOUNT);
			
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
    		
    	} catch (UnsupportedEncodingException e) {
    		LOGGER.severe(e.getMessage());
			throw new InternalServerErrorException(e);
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
	
	private static String showSalesforceOauth(Configuration configuration, Request request, Response response) {
		
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.templateName(Templates.SALESFORCE_OAUTH)
				.build();
		
		return template.render();
	}
}
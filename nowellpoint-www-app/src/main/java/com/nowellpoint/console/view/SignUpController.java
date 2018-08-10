package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import com.nowellpoint.oauth.OAuthClient;
import com.nowellpoint.oauth.model.AuthenticationRequest;
import com.nowellpoint.oauth.model.OAuthClientException;
import com.nowellpoint.oauth.model.OktaOAuthProvider;
import com.nowellpoint.oauth.model.TokenResponse;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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
		
		post(Path.Route.VERIFY, (request, response) 
				-> verify(configuration, request, response));
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
    	
    	Identity identity = ServiceClient.getInstance()
    			.identity()
    			.create(identityRequest);
    	
    	try {
			
    		AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
    				.password(password)
    				.username(email)
    				.build();
    		
    		OAuthClient client = OAuthClient.builder()
    				.provider(OktaOAuthProvider.builder().build())
    				.build();
    		
    		TokenResponse tokenResponse = client.authenticate(authenticationRequest);
    		
    		Jws<Claims> claims = client.getClaims(tokenResponse.getAccessToken());
    		
    		String jws = Jwts.builder()
    				.setHeaderParam("kid", claims.getHeader().getKeyId())
    				.setId(claims.getBody().getId())
    				.setIssuer(claims.getBody().getIssuer())
    				//.setAudience(identity.getOrganization().getId().toString())
    				.setSubject(identity.getId())
    				.setExpiration(claims.getBody().getExpiration())
    				.setIssuedAt(claims.getBody().getIssuedAt())
    				.claim("scope", claims.getBody().get("groups"))
    				.signWith(SignatureAlgorithm.HS256, "secret".getBytes("UTF-8"))
    				.compact();

    		Token token = Token.builder()
    				.environmentUrl(claims.getBody().getAudience())
    				.id(identity.getId().toString())
    				.accessToken(jws)
    				.expiresIn(tokenResponse.getExpiresIn())
    				.refreshToken(tokenResponse.getRefreshToken())
    				.tokenType(tokenResponse.getTokenType())
    				.build();
			
			Long expiresIn = token.getExpiresIn();
			
			try {			
				response.cookie("/", RequestAttributes.AUTH_TOKEN, new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true, true);
			} catch (IOException e) {
				throw new InternalServerErrorException(e);
			}
			
    	} catch (OAuthClientException e) {
    		
    		LOGGER.severe(e.getErrorDescription());
    		
    		Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", e.getErrorDescription());
			
    	} catch (UnsupportedEncodingException e) {
			throw new InternalServerErrorException(e);
		}

    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("registration", identity);
    	
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.model(model)
				.templateName(Templates.VERIFY)
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
	
	public static String verify(Configuration configuration, Request request, Response response) {
		
		String subject = request.queryParams("subject");
		
		Identity identity = ServiceClient.getInstance()
    			.identity()
    			.getBySubject(subject);
		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("identity", identity);
		
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.model(model)
				.templateName(Templates.VERIFY)
				.build();
		
		return template.render();
	}
	
	public static String resend(Configuration configuration, Request request, Response response) {
		
		String id = request.queryParams("id");
		
		ServiceClient.getInstance().identity().resendActivationEmail(id);
		
		Map<String, Object> model = new HashMap<String, Object>();
    	model.put("identity", null);
		
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.request(request)
				.model(model)
				.templateName(Templates.VERIFY)
				.build();
		
		return template.render();
	}
}
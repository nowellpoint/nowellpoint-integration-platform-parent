package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.exception.ConsoleException;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.RequestAttributes;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.oauth.model.OAuthClientException;
import com.nowellpoint.util.Properties;

import spark.Request;
import spark.Response;

public class AuthenticationController extends BaseController2 {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
	private static final String REDIRECT_URI = "redirect_uri";

	public AuthenticationController() {
		
		get(Path.Route.LOGIN, (request, response) 
				-> serveLoginPage(request, response));
		
		post(Path.Route.LOGIN, (request, response) 
				-> login(request, response));
		
		get(Path.Route.LOGOUT, (request, response) 
				-> logout(request, response));

	}
	
	private String login(Request request, Response response) {
		
		String username = request.queryParams("username");
		String password = request.queryParams("password");
		
		request.session().invalidate();
		
		try {
			
			Token token = ServiceClient.getInstance()
					.console()
					.authenticate(username, password.toCharArray());
			
			Long expiresIn = token.getExpiresIn();
			
			try {			
				response.cookie(System.getProperty(Properties.DOMAIN), "/", RequestAttributes.AUTH_TOKEN, Base64.getEncoder().encodeToString(new ObjectMapper().writeValueAsString(token).getBytes()), expiresIn.intValue(), true, true);
			} catch (IOException e) {
				throw new InternalServerErrorException(e);
			}
			
			if (request.queryParams(REDIRECT_URI) != null && !request.queryParams(REDIRECT_URI).isEmpty()) {
				response.redirect(request.queryParams(REDIRECT_URI));
			} else {
				response.redirect(Path.Route.START);
			}
			
			return "";
			
		} catch (OAuthClientException e) {

			String acceptLanguages = request.headers("Accept-Language");

			LOGGER.info(acceptLanguages);

			String[] languages = acceptLanguages.trim().replace("-", "_").split(";");

			String[] locales = languages[0].split(",");

			LOGGER.info(locales[0]);

			Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", e.getErrorDescription());
			
			ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
					.controllerClass(AuthenticationController.class)
					.model(model)
					.templateName(Templates.LOGIN)
					.build();
			
			return processTemplate(templateProcessRequest);

		} catch (ConsoleException e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	private String serveLoginPage(Request request, Response response) {
		Map<String, Object> model = getModel();
		model.put(REDIRECT_URI, request.queryParams(REDIRECT_URI) != null ? request.queryParams(REDIRECT_URI) : "");
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(AuthenticationController.class)
				.model(model)
				.templateName(Templates.LOGIN)
				.build();
		
		return processTemplate(templateProcessRequest);
    };
    
    private String logout(Request request, Response response) {
    	Optional<String> cookie = Optional.ofNullable(request.cookie(RequestAttributes.AUTH_TOKEN));

		if (cookie.isPresent()) {

			try {
				Token token = new ObjectMapper().readValue(Base64.getDecoder().decode(cookie.get()), Token.class);
				ServiceClient.getInstance().console().revoke(token.getAccessToken());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			response.removeCookie("/", RequestAttributes.AUTH_TOKEN);
		}

		request.session().invalidate();

		response.redirect(Path.Route.INDEX);

		return "";
    };
}
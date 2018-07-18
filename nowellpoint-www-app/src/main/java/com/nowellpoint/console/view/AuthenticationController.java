package com.nowellpoint.console.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.RequestAttributes;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.oauth.model.OAuthClientException;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

public class AuthenticationController {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationController.class.getName());
	private static final String REDIRECT_URI = "redirect_uri";

	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.LOGIN, (request, response) 
				-> serveLoginPage(configuration, request, response));
		
		post(Path.Route.LOGIN, (request, response) 
				-> login(configuration, request, response));
		
		get(Path.Route.LOGOUT, (request, response) 
				-> logout(configuration, request, response));

	}
	
	private static String login(Configuration configuration, Request request, Response response) {
		
		String username = request.queryParams("username");
		String password = request.queryParams("password");
		
		request.session().invalidate();
		
		try {
			
			Token token = ServiceClient.getInstance()
					.authentication()
					.authenticate(username, password);
			
			Long expiresIn = token.getExpiresIn();
			
			try {			
				response.cookie("/", RequestAttributes.AUTH_TOKEN, new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true, true);
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
			
			Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(AuthenticationController.class)
					.model(model)
					.request(request)
					.templateName(Templates.LOGIN)
					.build();
			
			return template.render();

		} catch (UnsupportedEncodingException e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	private static String serveLoginPage(Configuration configuration, Request request, Response response) {
		Map<String, Object> model = new HashMap<String,Object>();
		model.put(REDIRECT_URI, request.queryParams(REDIRECT_URI) != null ? request.queryParams(REDIRECT_URI) : "");
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(AuthenticationController.class)
				.model(model)
				.request(request)
				.templateName(Templates.LOGIN)
				.build();
		
		return template.render();
    };
    
    private static String logout(Configuration configuration, Request request, Response response) {
    	Optional<String> cookie = Optional.ofNullable(request.cookie(RequestAttributes.AUTH_TOKEN));

		if (cookie.isPresent()) {

			try {
				Token token = new ObjectMapper().readValue(cookie.get(), Token.class);
				ServiceClient.getInstance().authentication().revoke(token.getAccessToken());
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
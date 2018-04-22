package com.nowellpoint.www.app.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.AuthenticationService;
import com.nowellpoint.console.util.Templates;
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
	private static final String AUTH_TOKEN = "com.nowellpoint.auth.token";
	private static final String REDIRECT_URI = "redirect_uri";
	private static final AuthenticationService authenticationService = new AuthenticationService();

	public static void setupEndpoints(Configuration configuration) {
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
		
		Token token = null;
		try {
			token = authenticationService.authenticate(username, password);
		} catch (UnsupportedEncodingException e) {
			throw new InternalServerErrorException(e);
		}
		
		Long expiresIn = token.getExpiresIn();

		try {
			LOGGER.info(new ObjectMapper().writeValueAsString(token));
			response.cookie(AUTH_TOKEN, new ObjectMapper().writeValueAsString(token), expiresIn.intValue(), true);
		} catch (IOException e) {
			throw new InternalServerErrorException(e);
		}
		
		if (request.queryParams(REDIRECT_URI) != null && !request.queryParams(REDIRECT_URI).isEmpty()) {
			response.redirect(request.queryParams(REDIRECT_URI));
		} else {
			response.redirect(Path.Route.START);
		}
		
		return "";
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
    	Optional<String> cookie = Optional.ofNullable(request.cookie(AUTH_TOKEN));

		if (cookie.isPresent()) {

			try {
				Token token = new ObjectMapper().readValue(cookie.get(), Token.class);
				authenticationService.revoke(token.getAccessToken());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}

			response.removeCookie(AUTH_TOKEN);
		}

		request.session().invalidate();

		response.redirect(Path.Route.INDEX);

		return "";
    };
}
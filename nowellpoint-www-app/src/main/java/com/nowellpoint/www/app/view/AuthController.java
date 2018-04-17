package com.nowellpoint.www.app.view;

import com.nowellpoint.console.service.AuthenticationService;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;

import static spark.Spark.get;
import static spark.Spark.post;

public class AuthController {
	
	private final Configuration configuration;
	private final AuthenticationService authenticationService;
	
	public AuthController(Configuration configuration, AuthenticationService authenticationService) {
		this.configuration = configuration;
		this.authenticationService = authenticationService;
		setupEndpoints();
	}

	private void setupEndpoints() {
		get(Path.Route.LOGIN, (request, response) 
				-> AuthenticationController.serveLoginPage(configuration, request, response));
		
		post(Path.Route.LOGIN, (request, response) 
				-> authenticationService.authentication(request, response));
	}
}
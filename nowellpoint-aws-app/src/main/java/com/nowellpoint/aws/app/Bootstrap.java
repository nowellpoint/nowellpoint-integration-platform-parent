package com.nowellpoint.aws.app;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.service.IdentityProviderService;
import com.nowellpoint.aws.service.SalesforceService;
import com.nowellpoint.aws.sforce.model.GetAuthorizationRequest;
import com.nowellpoint.aws.sforce.model.GetAuthorizationResponse;
import com.nowellpoint.aws.sforce.model.GetIdentityRequest;
import com.nowellpoint.aws.sforce.model.GetIdentityResponse;
import com.nowellpoint.aws.sforce.model.Token;

import freemarker.template.Configuration;

public class Bootstrap {

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static SalesforceService sforceService = new SalesforceService();

	private static IdentityProviderService identityProviderService = new IdentityProviderService();

	public static void main(String[] args) {

		//
		// Configure FreeMarker
		//

		Configuration cfg = new Configuration();

		//
		// set configuration options
		//

		cfg.setClassForTemplateLoading(Bootstrap.class, "/views");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);

		//
		// port and keystore
		//

		port(getPort());
		// secure(keystoreFile, keystorePassword, truststoreFile,
		// truststorePassword);

		//
		// add static file location
		//

		staticFileLocation("/public");

		//
		//
		//

		addRoutes(cfg);
	}

	private static void addRoutes(Configuration cfg) {

		//
		// add resource bundle
		//

		ResourceBundle messages = ResourceBundle.getBundle("messages",
				Locale.US);

		//
		// add properties to model
		//

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("applicationTitle",
				messages.getString("application.title"));
		attributes.put("services", messages.getString("services"));

		//
		// add routes for root
		//

		get("/", (request, response) -> {
			System.out.println(request.protocol());
			System.out.println(request.uri());

			return new ModelAndView(attributes, "index.ftl");
		}, new FreeMarkerEngine(cfg));

		//
		//
		//

		get("/callback",
				(request, response) -> {

					GetAuthorizationRequest authorizationRequest = new GetAuthorizationRequest()
							.withCode(request.queryParams("code"));

					GetAuthorizationResponse authorizationResponse = sforceService
							.authorize(authorizationRequest);

					response.status(authorizationResponse.getStatusCode());

					if (authorizationResponse.getStatusCode() == 200) {
						Token token = authorizationResponse.getToken();

						GetIdentityRequest identityRequest = new GetIdentityRequest()
								.withAccessToken(token.getAccessToken())
								.withId(token.getId());

						GetIdentityResponse identityResponse = sforceService
								.getIdentity(identityRequest);

						if (identityResponse.getStatusCode() < 400) {
							attributes.put("identity",
									identityResponse.getIdentity());
						} else {
							attributes.put("exception",
									identityResponse.getErrorMessage());
						}

					}

					return new ModelAndView(attributes, "identity.ftl");

				});

		//
		//
		//

		get("/identity",
				(request, response) -> {

					GetIdentityRequest identityRequest = new GetIdentityRequest()
							.withAccessToken(
									request.headers("Authorization")
											.replaceFirst("Bearer", "").trim())
							.withId(request.queryParams("id"));

					GetIdentityResponse identityResponse = sforceService
							.getIdentity(identityRequest);

					if (identityResponse.getStatusCode() < 400) {
						attributes.put("identity",
								identityResponse.getIdentity());
					} else {
						attributes.put("exception",
								identityResponse.getErrorMessage());
					}

					return new ModelAndView(attributes, "identity.ftl");
				}, new FreeMarkerEngine(cfg));

		//
		//
		//

		get("/login",
				(request, response) -> {

					GetTokenRequest tokenRequest = new GetTokenRequest()
							.withUsername(request.queryParams("username"))
							.withPassword(request.queryParams("password"));

					GetTokenResponse getTokenResponse = identityProviderService
							.authenticate(tokenRequest);

					response.status(getTokenResponse.getStatusCode());

					if (getTokenResponse.getStatusCode() == 200) {
						response.cookie("nowellpoint.token",
								objectMapper
										.writeValueAsString(getTokenResponse
												.getToken()), 0, Boolean.TRUE);
					} else {
						response.body(getTokenResponse.getErrorMessage());
					}

					return new ModelAndView(attributes, "index.ftl");

				}, new FreeMarkerEngine(cfg));
	}

	private static int getPort() {
		String port = Optional.ofNullable(System.getenv().get("PORT")).orElse(
				"8080");
		return Integer.parseInt(port);
	}
}
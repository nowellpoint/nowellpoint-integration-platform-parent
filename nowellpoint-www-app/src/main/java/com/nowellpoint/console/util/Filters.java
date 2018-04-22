package com.nowellpoint.console.util;

import static spark.Spark.halt;

import java.net.URLEncoder;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.IdentityService;
import com.nowellpoint.www.app.util.Path;

import spark.Filter;
import spark.Request;
import spark.Response;

public class Filters {
	
	private static final IdentityService identityService = new IdentityService();
	private static final String AUTH_TOKEN = "com.nowellpoint.auth.token";
	private static final String IDENTITY = "com.nowellpoint.auth.identity";
	private static final String LOCALE = "com.nowellpoint.default.locale";
	private static final String TIME_ZONE = "com.nowellpoint.default.timezone";
	private static final String REDIRECT_URI = "redirect_uri";
	
	public static Filter addTrailingSlashes = (Request request, Response response) -> {
        if (!request.pathInfo().endsWith("/")) {
            response.redirect(request.pathInfo() + "/");
        }
    };
    
    public static Filter setDefaultAttributes = (Request request, Response response) -> {
		request.attribute(LOCALE, Locale.getDefault());
		request.attribute(TIME_ZONE, TimeZone.getDefault());
	};
	
	public static Filter authenticatedUser = (Request request, Response response) -> {

		Optional<String> cookie = Optional.ofNullable(request.cookie(AUTH_TOKEN));
		
		if (cookie.isPresent()) {

			Token token = new ObjectMapper().readValue(cookie.get(), Token.class);

			request.attribute(AUTH_TOKEN, token);
			
			Identity identity = identityService.getIdentity(token.getId());

			request.attribute(IDENTITY, identity);
			request.attribute(LOCALE, identity.getLocale() != null ? identity.getLocale() : Locale.getDefault());
			request.attribute(TIME_ZONE, identity.getTimeZone() != null ? TimeZone.getTimeZone(identity.getTimeZone()) : TimeZone.getDefault());

		} else {
			
			response.redirect(Path.Route.LOGIN.concat("?")
					.concat(REDIRECT_URI).concat("=")
					.concat(URLEncoder.encode(request.pathInfo(), "UTF-8")));
			
			halt();
		}
	};
}
package com.nowellpoint.console.util;

import static spark.Spark.before;
import static spark.Spark.halt;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.IdentityService;
import com.nowellpoint.www.app.util.Path;

import spark.Request;
import spark.Response;

public class Filters {
	
	private static final IdentityService identityService = new IdentityService();
	private static final String REDIRECT_URI = "redirect_uri";
	
	public static void setupFilters() {
		before("/*",     (request, response) -> addTrailingSlashes(request, response));
		before("/*",     (request, response) -> setDefaultAttributes(request, response));
		before("/app/*", (request, response) -> verifyUser(request, response));
	}
	
	private static void addTrailingSlashes(Request request, Response response) {
        if (!request.pathInfo().endsWith("/")) {
            response.redirect(request.pathInfo() + "/");
        }
    }
    
	private static void setDefaultAttributes(Request request, Response response) {
		request.attribute(RequestAttributes.LOCALE, Locale.getDefault());
		request.attribute(RequestAttributes.TIME_ZONE, TimeZone.getDefault());
	}
	
	private static void verifyUser(Request request, Response response) throws JsonParseException, JsonMappingException, IOException {
		Optional<String> cookie = Optional.ofNullable(request.cookie(RequestAttributes.AUTH_TOKEN));
		
		if (cookie.isPresent()) {
			
			Token token = new ObjectMapper().readValue(cookie.get(), Token.class);
			
			Identity identity = identityService.getIdentity(token.getId());
			
			Locale locale =  Optional.ofNullable(identity.getLocale()).orElse(request.attribute(RequestAttributes.LOCALE));
			
			TimeZone timeZone = identity.getTimeZone() != null ? TimeZone.getTimeZone(identity.getTimeZone()) : request.attribute(RequestAttributes.TIME_ZONE);

			request.attribute(RequestAttributes.AUTH_TOKEN, token);
			request.attribute(RequestAttributes.IDENTITY, identity);
			request.attribute(RequestAttributes.LOCALE, locale);
			request.attribute(RequestAttributes.TIME_ZONE, timeZone);

		} else {

			response.redirect(Path.Route.LOGIN.concat("?")
					.concat(REDIRECT_URI).concat("=")
					.concat(URLEncoder.encode(request.pathInfo(), "UTF-8")));
			
			halt();
		}
	}
}
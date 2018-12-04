package com.nowellpoint.console.util;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.halt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.eclipse.jetty.http.HttpStatus;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Token;
import com.nowellpoint.console.service.ServiceClient;


import spark.Request;
import spark.Response;

public class Filters {
	
	private static final Logger LOGGER = Logger.getLogger(Filters.class.getName());
	private static final String REDIRECT_URI = "redirect_uri";
	
	public static void setupFilters() {
		before("/*",     (request, response) -> addTrailingSlashes(request, response));
		before("/*",     (request, response) -> setDefaultAttributes(request, response));
		before("/app/*", (request, response) -> verifyUser(request, response));
		after("/app/*",  (request, response) -> logRequest(request, response));
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

			Token token = new ObjectMapper().readValue(Base64.getDecoder().decode(cookie.get()), Token.class);
			
			Identity identity = ServiceClient.getInstance()
					.identity()
					.get(token.getId());
			
			Locale locale =  Optional.ofNullable(identity.getLocale()).orElse(request.attribute(RequestAttributes.LOCALE));
			
			TimeZone timeZone = identity.getTimeZone() != null ? TimeZone.getTimeZone(identity.getTimeZone()) : request.attribute(RequestAttributes.TIME_ZONE);

			request.attribute(RequestAttributes.AUTH_TOKEN, token);
			request.attribute(RequestAttributes.IDENTITY, identity);
			request.attribute(RequestAttributes.LOCALE, locale);
			request.attribute(RequestAttributes.TIME_ZONE, timeZone);
			
			UserContext.set(identity);
			
			System.out.println(request.pathInfo() + " " + request.pathInfo().endsWith("/activate/"));
			
		//	if (!request.pathInfo().equals(Path.Route.ACCOUNT_ACTIVATE) && identity.getActive() == Boolean.FALSE) {
		//		response.redirect(Path.Route.ACCOUNT_ACTIVATE);
		//	}

		} else {

			response.redirect(Path.Route.LOGIN.concat("?")
					.concat(REDIRECT_URI).concat("=")
					.concat(URLEncoder.encode(request.pathInfo(), "UTF-8")));
			
			halt();
		}
	}
	
	private static void logRequest(Request request, Response response) {
		
		final String subject = UserContext.get() != null ? UserContext.get().getId() : null;
		final String path = request.pathInfo().concat(request.queryString() != null ? "?".concat(request.queryString()) : "");
		final Integer statusCode = response.status();
		final String statusMessage = HttpStatus.getMessage(statusCode);
		final String requestMethod = request.requestMethod();
		final String userAgent = request.userAgent();
		
		ObjectNode node = JsonNodeFactory.instance.objectNode()
				.put("hostname", request.host())
				.put("subject", subject)
				.put("date", System.currentTimeMillis())
				.put("method", requestMethod)
				.put("path", path)
				.put("userAgent", userAgent)
				.put("statusCode", statusCode)
				.put("statusMessage", statusMessage);
		
		writeLogEntry("api", node.toString());
		
		if (UserContext.get() != null) {
			UserContext.unset();
		}
	}
	
	private static void writeLogEntry(String tag, String logEntry) {
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					
					HttpURLConnection connection = (HttpURLConnection) new URL(SecretsManager.getLogglyApiEndpoint()
							.concat("/")
							.concat(SecretsManager.getLogglyApiKey())
							.concat("/")
							.concat(tag)
							.concat("/api")
							.concat("/")
					).openConnection();
					
					connection.setRequestMethod("GET");
					connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
					connection.setDoOutput(true);
					
					byte[] outputInBytes = logEntry.toString().getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write( outputInBytes );    
					os.close();
					
					connection.connect();
					
					if (connection.getResponseCode() != 200) {
						LOGGER.severe(IOUtils.toString(connection.getErrorStream()));
					}
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
				}
			}
			
		});	
	}
}
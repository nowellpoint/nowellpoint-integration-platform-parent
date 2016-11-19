package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SignUpController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
	
	public static class Template {
		public static final String SIGN_UP = "signup.html";
	}
	
	public SignUpController() {
		super(SignUpController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.SIGN_UP, (request, response) -> showSignUp(configuration, request, response));
		post(Path.Route.SIGN_UP, (request, response) -> signUp(configuration, request, response));
	}
	
	private String showSignUp(Configuration configuration, Request request, Response response) {
		Map<String, Object> model = new HashMap<String, Object>();
		return render(configuration, request, response, model, Template.SIGN_UP);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	
	private String signUp(Configuration configuration, Request request, Response response) throws UnsupportedEncodingException {
		Map<String, Object> model = new HashMap<String, Object>();
    	
    	if (request.queryParams("password").equals(request.queryParams("confirmPassword"))) {
    		      		
    		String body = new StringBuilder()
    				.append("leadSource=")
    				.append(request.queryParams("leadSource"))
    				.append("&firstName=")
    				.append(URLEncoder.encode(request.queryParams("firstName"), "UTF-8"))
    				.append("&lastName=")
    				.append(URLEncoder.encode(request.queryParams("lastName"), "UTF-8"))
    				.append("&email=")
    				.append(request.queryParams("email"))
    				.append("&countryCode=")
    				.append(request.queryParams("countryCode"))
    				.append("&password=")
    				.append(URLEncoder.encode(request.queryParams("password"), "UTF-8"))
    				.toString();
        	
    		HttpResponse httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
        			.header("x-api-key", System.getenv("NCS_API_KEY"))
        			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        			.acceptCharset("UTF-8")
        			.path("signup")
        			.body(body)
        			.execute();
        	
        	LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
        		        	
        	if (httpResponse.getStatusCode() == 200) {
        		model.put("successMessage", MessageProvider.getMessage(Locale.US, "signUpConfirm"));   
        	} else {
        		JsonNode error = httpResponse.getEntity(JsonNode.class);
        		model.put("errorMessage", error.get("message").asText());
        	}
        	
    	} else {
    		model.put("errorMessage", MessageProvider.getMessage(Locale.US, "passwordMismatch"));
    	}
    	
    	return render(configuration, request, response, model, Template.SIGN_UP);
	}
}
package com.nowellpoint.www.app.view;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class SignUpController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
	
	public SignUpController(Configuration configuration) {
		super(SignUpController.class, configuration);
	}
	
	public Route showSignUp = (Request request, Response response) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		return render(request, model, Path.Template.SIGN_UP);
	};
	
	public Route signUp = (Request request, Response response) -> {
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
        	
    		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
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
    	
    	return render(request, model, Path.Template.SIGN_UP);
	};
}
package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class VerifyEmailController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(VerifyEmailController.class.getName());

	public VerifyEmailController(Configuration configuration) {
		super(VerifyEmailController.class, configuration);
	}

	@Override
	public void configureRoutes(Configuration configuration) {
        
        get("/verify-email", (request, response) -> getVerifyEmail(request, response), new FreeMarkerEngine(configuration));
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	
	private ModelAndView getVerifyEmail(Request request, Response response) throws JsonParseException, JsonMappingException, IOException {
    	
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.path("signup")
				.path("verify-email")
				.path(request.queryParams("emailVerificationToken"))
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		Map<String,Object> model = getModel();
		
    	if (httpResponse.getStatusCode() == Status.OK) {
    		model.put("successMessage", "Your email has been verified.");
    	} else {
    		model.put("errorMessage", "We are unable to verify your email address.");
    	}
    	
		return new ModelAndView(model, "verify-email.html");
	}
}
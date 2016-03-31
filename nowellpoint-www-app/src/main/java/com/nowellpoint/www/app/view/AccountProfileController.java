package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.delete;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.AccountProfile;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class AccountProfileController {
	
	private static final Logger LOGGER = Logger.getLogger(ProjectController.class.getName());
	
	public AccountProfileController(Configuration cfg) {
	    
		get("/app/account-profile", (request, response) -> getAccountProfile(request, response), new FreeMarkerEngine(cfg));
		
		post("/app/account-profile", (request, response) -> updateAccountProfile(request, response), new FreeMarkerEngine(cfg));
		
		post("/app/account-profile/picture/salesforce", (request, response) -> setSalesforceProfilePicture(request, response));
		
		delete("/app/account-profile/picture", (request, response) -> removeProfilePicture(request, response));
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView getAccountProfile(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
			
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("accountProfile", accountProfile);
			
		return new ModelAndView(model, "secure/account-profile.html");			
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	public static ModelAndView updateAccountProfile(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		StringBuilder body = new StringBuilder();
		request.queryParams().stream().filter(param -> ! request.queryParams(param).isEmpty()).limit(1).forEach(param -> {
			body.append(param).append("=").append(request.queryParams(param));
		});
		request.queryParams().stream().filter(param -> ! request.queryParams(param).isEmpty()).skip(1).forEach(param -> {
			body.append("&").append(param).append("=").append(request.queryParams(param));
    	});

		HttpResponse httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.acceptCharset("UTF-8")
				.path("account-profile")
				.body(body.toString())
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo() + " : " + httpResponse.getHeaders().get("Location"));
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode() && httpResponse.getStatusCode() != Status.CREATED.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("accountProfile", accountProfile);
		
		return new ModelAndView(model, "secure/account-profile.html");		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String setSalesforceProfilePicture(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
    			.bearerAuthorization(token.getAccessToken())
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        		.acceptCharset("UTF-8")
        		.path("account-profile")
        		.path("photo")
        		.path("salesforce")
        		.body("photoUrl=".concat(request.queryParams("photoUrl")))
        		.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " Location: " + httpResponse.getHeaders().get("Location"));
		
		return "";	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView removeProfilePicture(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
    			.bearerAuthorization(token.getAccessToken())
        		.path("account-profile")
        		.path("photo")
        		.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " Location: " + httpResponse.getHeaders().get("Location"));
		
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		
		model.put("accountProfile", accountProfile);
		
		return new ModelAndView(model, "secure/account-profile.html");		
	}
}
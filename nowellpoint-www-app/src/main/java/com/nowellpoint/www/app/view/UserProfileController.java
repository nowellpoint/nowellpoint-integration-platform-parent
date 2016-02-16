package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Identity;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class UserProfileController {
	
	private static final Logger LOGGER = Logger.getLogger(ProjectController.class.getName());
	
	public UserProfileController(Configuration cfg) {
		
		//
	    // GET
	    //
	    
		get("/app/user-profile", (request, response) -> {
			
			Token token = request.attribute("token");
			
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.path("identity")
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
			
			if (httpResponse.getStatusCode() != Response.Status.OK.getStatusCode()) {
				throw new NotFoundException(httpResponse.getEntity());
			}
			
			Identity identity = httpResponse.getEntity(Identity.class);
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("account", request.attribute("account"));
			model.put("identity", identity);
			
			return new ModelAndView(model, "secure/user-profile.html");
			
		}, new FreeMarkerEngine(cfg));
		
		//
		// POST
		//
		
		post("/app/user-profile", (request, response) -> {
			
			Token token = request.attribute("token");
			
			StringBuilder body = new StringBuilder();
			request.queryParams().stream().limit(1).forEach(param -> {
				body.append(param).append("=").append(request.queryParams(param));
			});
			request.queryParams().stream().skip(1).forEach(param -> {
				body.append("&").append(param).append("=").append(request.queryParams(param));
        	});

			HttpResponse httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        			.acceptCharset("UTF-8")
					.path("user-profile")
					.body(body.toString())
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo() + " : " + httpResponse.getHeaders().get("Location"));
			
			if (httpResponse.getStatusCode() != Response.Status.OK.getStatusCode() && httpResponse.getStatusCode() != Response.Status.CREATED.getStatusCode()) {
				throw new BadRequestException(httpResponse.getEntity());
			}
			
			Identity identity = httpResponse.getEntity(Identity.class);
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("account", request.attribute("account"));
			model.put("identity", identity);
			
			return new ModelAndView(model, "secure/user-profile.html");
			
		}, new FreeMarkerEngine(cfg));
		
	}
}
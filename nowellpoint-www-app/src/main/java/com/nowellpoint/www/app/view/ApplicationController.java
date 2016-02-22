package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Application;
import com.nowellpoint.www.app.model.Identity;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ApplicationController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationController.class);
	
	public ApplicationController(Configuration cfg) {
		
		/**
		 * setup routes
		 */
    
		get("/app/applications", (request, response) -> getApplications(request, response), new FreeMarkerEngine(cfg));
		
		get("/app/applications/types", (request, response) -> routeToType(request, response), new FreeMarkerEngine(cfg));
		
		post("/app/applications", (request, response) -> saveApplication(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView getApplications(Request request, Response response) throws IOException {
		Token token = request.attribute("token");
		
		LOGGER.info(token.getAccessToken());
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("identity")
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getEntity());
		}
		
		Identity identity = httpResponse.getEntity(Identity.class);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("applicationList", identity.getApplications());
		
		return new ModelAndView(model, "secure/application-list.html");
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView routeToType(Request request, Response response) {
		
		String type = request.params(":type");
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		
		return new ModelAndView(model, String.format("secure/application-types.html", type));
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String saveApplication(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		Application application = new Application();
		application.setType(request.queryParams("type"));
		application.setIsSandbox(Boolean.FALSE);
					
		HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON)
				.path("application")
				.body(application)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		response.redirect("/app/applications");
		
		return "";
	}
}
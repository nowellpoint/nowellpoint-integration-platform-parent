package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceController extends AbstractController {
	
	private final static Logger LOG = LoggerFactory.getLogger(SalesforceController.class.getName());
	
	public SalesforceController(Configuration cfg) {
		
		super(SalesforceController.class, cfg);
		
		get("/app/salesforce/oauth", (request, response) -> oauth(request, response));
        
        get("/app/salesforce/callback", (request, response) -> callback(request, response), new FreeMarkerEngine(cfg));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private String oauth(Request request, Response response) {
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
    			.header("x-api-key", System.getenv("NCS_API_KEY"))
    			.path("salesforce")
    			.path("oauth")
    			.queryParameter("state", request.queryParams("id"))
    			.execute();
    	
    	LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " : " + httpResponse.getHeaders().get("Location"));
		
		response.redirect(httpResponse.getHeaders().get("Location").get(0));		
		
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private ModelAndView callback(Request request, Response response) {
    	
    	Optional<String> code = Optional.ofNullable(request.queryParams("code"));
    	
    	if (! code.isPresent()) {
    		throw new BadRequestException("missing OAuth code from Salesforce");
    	}
    	
		return new ModelAndView(model, "secure/salesforce-callback.html");	
    }
}
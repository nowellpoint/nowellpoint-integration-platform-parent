package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class ContactController {
	
	private static final Logger logger = Logger.getLogger(ContactController.class.getName());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public ContactController(Configuration cfg) {
		
        //
        // GET
        //
        
		get("/contact", (request, response) -> {
			Map<String, Object> model = new HashMap<String, Object>();
			return new ModelAndView(model, "contact.html");
		}, new FreeMarkerEngine(cfg));
        
        //
        // POST
        //
        
        post("/contact", (request, response) -> {
        	
        	ObjectNode body = objectMapper.createObjectNode();
        	request.queryParams().stream().forEach(param -> {
        		body.put(param, request.queryParams(param));
        	});
        	
        	logger.info(body.toString());
        	
        	HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
        			.header("x-api-key", System.getenv("NCS_API_KEY"))
        			.contentType(MediaType.APPLICATION_JSON)
        			.path("lead")
        			.body(body.toString())
        			.execute();
        	
        	logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo() + " : " + httpResponse.getHeaders().get("Location"));
        	
        	Map<String, Object> model = new HashMap<String, Object>();
        	model.put("successMessage", MessageProvider.getMessage(Locale.US, "contactConfirm"));
        	return new ModelAndView(model, "contact.html");
        	
        }, new FreeMarkerEngine(cfg));
	}
}
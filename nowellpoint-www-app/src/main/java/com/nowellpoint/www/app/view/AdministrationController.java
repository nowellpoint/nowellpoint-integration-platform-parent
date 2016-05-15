package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Property;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class AdministrationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AdministrationController.class);
	
	public AdministrationController(Configuration cfg) {
		
		super(AdministrationController.class, cfg);
		
		get("/app/administration", (request, response) -> getProperties(request, response), new FreeMarkerEngine(cfg));	
			
	}
	
	private ModelAndView getProperties(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("properties")
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<Property> properties = httpResponse.getEntityList(Property.class);
		
		try {
			System.out.println(new ObjectMapper().writeValueAsString(properties));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("properties size: " + properties.size());
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("propertyList", properties);
		
		return new ModelAndView(model, "secure/properties-list.html");
	}
}
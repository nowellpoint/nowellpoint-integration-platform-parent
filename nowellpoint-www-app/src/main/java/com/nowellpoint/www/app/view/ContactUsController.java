package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class ContactUsController extends AbstractController {
	
	private static final Logger logger = Logger.getLogger(ContactUsController.class.getName());
	
	public static class Template {
		
	}
	
	public ContactUsController() {
		super(ContactUsController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.CONTACT_US, (request, response) -> showContactUs(configuration, request, response));
		post(Path.Route.CONTACT_US, (request, response) -> contactUs(configuration, request, response));
	}
	
	private String showContactUs(Configuration configuration, Request request, Response response) {
		Map<String, Object> model = new HashMap<String, Object>();
		return render(configuration, request, response, model, Path.Template.CONTACT_US);
	};
	
	private String contactUs(Configuration configuration, Request request, Response response) {
		ObjectNode body = objectMapper.createObjectNode();
    	request.queryParams().stream().forEach(param -> {
    		body.put(param, request.queryParams(param));
    	});
    	
    	HttpResponse httpResponse = RestResource.post(System.getenv("NOWELLPOINT_API_ENDPOINT"))
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.path("contact")
				.parameter("leadSource", request.queryParams("leadSource"))
				.parameter("firstName", request.queryParams("firstName"))
				.parameter("lastName", request.queryParams("lastName"))
				.parameter("email", request.queryParams("email"))
				.parameter("phone", request.queryParams("phone"))
				.parameter("company", request.queryParams("company"))
				.parameter("description", request.queryParams("description"))
    			.execute();
    	
    	logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo() + " : " + httpResponse.getHeaders().get("Location"));
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("successMessage", MessageProvider.getMessage(Locale.US, "contactConfirm"));
    	return render(configuration, request, response, model, Path.Template.CONTACT_US);
	};
}
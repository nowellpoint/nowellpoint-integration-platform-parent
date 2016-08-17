package com.nowellpoint.www.app.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import spark.Route;

public class ContactUsController extends AbstractController {
	
	private static final Logger logger = Logger.getLogger(ContactUsController.class.getName());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public ContactUsController(Configuration cfg) {
		super(ContactUsController.class, cfg);
	}
	
	public Route showContactUs = (Request request, Response response) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		return render(request, model, Path.Template.CONTACT_US);
	};
	
	public Route contactUs = (Request request, Response response) -> {
		ObjectNode body = objectMapper.createObjectNode();
    	request.queryParams().stream().forEach(param -> {
    		body.put(param, request.queryParams(param));
    	});
    	
    	logger.info(body.toString());
    	
    	HttpResponse httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
    			.header("x-api-key", System.getenv("NCS_API_KEY"))
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
    	return render(request, model, Path.Template.CONTACT_US);
	};
}
package com.nowellpoint.www.app.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Application;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class NotificationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(NotificationController.class);
	
	public NotificationController(Configuration cfg) {
		super(NotificationController.class, cfg);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		
	}
	
	public Route showNotifications = (Request request, Response response) -> {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("application")
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<Application> applications = httpResponse.getEntityList(Application.class);
		
		applications = applications.stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toList());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("applicationList", applications);
		
		return render(request, model, Path.Template.NOTIFICATIONS);
	};
}
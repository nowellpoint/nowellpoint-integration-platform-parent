package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.client.model.Application;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class NotificationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(NotificationController.class.getName());
	
	public static class Template {
		public static final String NOTIFICATIONS = String.format(APPLICATION_CONTEXT, "notification-list.html");
	}
	
	public NotificationController(Configuration configuration) {
		super(NotificationController.class);
		configureRoutes(configuration);
	}
	
	private void configureRoutes(Configuration configuration) {
		get(Path.Route.NOTIFICATIONS, (request, response) -> showNotifications(configuration, request, response));
	}
	
	private String showNotifications(Configuration configuration, Request request, Response response) {
		
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
		
		return render(configuration, request, response, model, Template.NOTIFICATIONS);
	};
}
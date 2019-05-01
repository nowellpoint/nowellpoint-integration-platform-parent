package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.util.List;
import java.util.Map;

import com.nowellpoint.console.model.Notification;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;

import spark.Request;
import spark.Response;

public class NotificationController extends BaseController {
	
	public static void configureRoutes() {
		get(Path.Route.NOTIFICATIONS, (request, response)
				-> viewNotifications(request, response));
	}
	
	private static String viewNotifications(Request request, Response response) {
		
		String organizationId = getIdentity(request).getOrganization().getId();
		
		List<Notification> notifications = ServiceClient.getInstance()
				.notification()
				.getNotifications(organizationId);
		
		Map<String,Object> model = getModel();
		model.put("notifications", notifications);
    	
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(NotificationController.class)
				.model(model)
				.templateName(Templates.NOTIFICATIONS)
				.build();
		
		return processTemplate(templateProcessRequest);
	}
}
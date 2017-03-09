package com.nowellpoint.www.app.view;

import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class NotificationController extends AbstractStaticController {
		
	public static class Template {
		public static final String NOTIFICATIONS = String.format(APPLICATION_CONTEXT, "notification-list.html");
	}
	
	public static String serveNotificationsPage(Configuration configuration, Request request, Response response) {
		//Token token = getToken(request);
		
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		return render(NotificationController.class, configuration, request, response, model, Template.NOTIFICATIONS);
	};
}
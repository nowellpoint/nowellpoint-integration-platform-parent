package com.nowellpoint.console.util;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;

import com.nowellpoint.console.model.Template;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.view.AuthenticationController;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class Exceptions {
	
	public static void setupEndpoints(Configuration configuration) {
		get("*", (request, response) -> serveNotFoundPage(configuration, request, response));
	}
	
	private static String serveNotFoundPage(Configuration configuration, Request request, Response response) {
		response.status(HttpStatus.NOT_FOUND_404);
		
		Map<String, Object> model = new HashMap<String,Object>();
		model.put("errorMessage", MessageProvider.getMessage(request.attribute("com.nowellpoint.default.locale"), "page.not.found"));
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(AuthenticationController.class)
				.model(model)
				.request(request)
				.templateName(Templates.NOT_FOUND)
				.build();
		
		return template.render();
    };
}
package com.nowellpoint.console.view;

import static spark.Spark.get;

import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class StartController {
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.START, (request, response) -> viewStartPage(configuration, request, response));
	}

	private static String viewStartPage(Configuration configuration, Request request, Response response) {
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(StartController.class)
				.request(request)
				.templateName(Templates.START)
				.build();
    	
    	return template.render();
	};	
}
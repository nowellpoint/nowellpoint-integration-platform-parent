package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Map;

import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class StartController extends AbstractStaticController {
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.START, (request, response) -> serveStartPage(configuration, request, response));
	}

	private static String serveStartPage(Configuration configuration, Request request, Response response) {
    	Map<String,Object> model = getModel();
    	
    	Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(StartController.class)
				.model(model)
				.request(request)
				.templateName(Templates.START)
				.build();
    	
    	return template.render();
	};	
}
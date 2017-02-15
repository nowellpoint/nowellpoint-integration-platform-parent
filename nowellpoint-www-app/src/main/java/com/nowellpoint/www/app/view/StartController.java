package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Map;

import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class StartController extends AbstractStaticController {
	
	public static class Template {
		public static final String START = String.format(APPLICATION_CONTEXT, "start.html");
	}
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.START, (request, response) -> showStartPage(configuration, request, response));
	}

	private static String showStartPage(Configuration configuration, Request request, Response response) {
    	Map<String,Object> model = getModel();
    	return render(StartController.class, configuration, request, response, model, Template.START);
	};	
}
package com.nowellpoint.www.app.view;

import java.util.Map;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class StartController extends AbstractStaticController {
	
	public static class Template {
		public static final String START = String.format(APPLICATION_CONTEXT, "start.html");
	}

	public static String serveStartPage(Configuration configuration, Request request, Response response) {
    	Map<String,Object> model = getModel();
    	return render(StartController.class, configuration, request, response, model, Template.START);
	};	
}
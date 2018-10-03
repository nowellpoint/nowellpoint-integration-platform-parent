package com.nowellpoint.console.view;

import static spark.Spark.get;

import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import spark.Request;
import spark.Response;

public class StartController extends BaseController {
	
	public static void configureRoutes() {
		get(Path.Route.START, (request, response) -> viewStartPage(request, response));
	}

	private static String viewStartPage(Request request, Response response) {
    	
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(StartController.class)
				.templateName(Templates.START)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
}
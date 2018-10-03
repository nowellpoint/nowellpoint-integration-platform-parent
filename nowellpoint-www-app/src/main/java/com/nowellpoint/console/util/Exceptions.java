package com.nowellpoint.console.util;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.view.AuthenticationController;
import com.nowellpoint.console.view.BaseController;
import com.nowellpoint.www.app.util.MessageProvider;

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

public class Exceptions extends BaseController {
	
	public static void configureExceptionRoutes() {
		get("*", (request, response) -> serveNotFoundPage(request, response));
	}
	
	private static String serveNotFoundPage(Request request, Response response) {
		response.status(HttpStatus.NOT_FOUND_404);
		
		Map<String, Object> model = new HashMap<String,Object>();
		model.put("errorMessage", MessageProvider.getMessage(request.attribute(RequestAttributes.LOCALE), "page.not.found"));

		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(AuthenticationController.class)
				.model(model)
				.templateName(Templates.NOT_FOUND)
				.build();

		return processTemplate(templateProcessRequest);
    };
}
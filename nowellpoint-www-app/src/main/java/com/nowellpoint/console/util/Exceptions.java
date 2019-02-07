package com.nowellpoint.console.util;

import static spark.Spark.exception;
import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.console.exception.ServiceException;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.view.AuthenticationController;
import com.nowellpoint.console.view.BaseController;

import com.okta.sdk.resource.ResourceException;

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

public class Exceptions extends BaseController {
	
	public static void configureExceptionRoutes() {
		
		exception(ServiceException.class, (exception, request, response) -> {
			serviceException(exception, request, response);
		});
		
		exception(ResourceException.class, (exception, request, response) -> {
			resourceException(exception, request, response);
		});
		
		
		get("*", (request, response) -> servePageNotFound(request, response));
	}
	
	private static String servePageNotFound(Request request, Response response) {
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
    
    private static String serviceException(ServiceException exception, Request request, Response response) {
		response.status(HttpStatus.BAD_REQUEST_400);
		
		Map<String, Object> model = new HashMap<String,Object>();
		model.put("errorMessage", exception.getMessage());

		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(AuthenticationController.class)
				.model(model)
				.templateName(Templates.ERROR)
				.build();

		return processTemplate(templateProcessRequest);
    };
    
    private static String resourceException(ResourceException exception, Request request, Response response) {
		response.status(HttpStatus.BAD_REQUEST_400);
		
		Map<String, Object> model = new HashMap<String,Object>();
		model.put("errorMessage", exception.getMessage());

		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(AuthenticationController.class)
				.model(model)
				.templateName(Templates.ERROR)
				.build();

		return processTemplate(templateProcessRequest);
    };
}
package com.nowellpoint.console.util;

import static spark.Spark.exception;

import java.time.zone.ZoneRulesException;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

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
		
		exception(BadRequestException.class, (exception, request, response) -> {
			response.status(400);
			response.body(exception.getMessage());
		});
		
		exception(NotFoundException.class, (exception, request, response) -> {
			notFoundExeption(exception, request, response);
		});
		
		exception(ServiceException.class, (exception, request, response) -> {
			serviceException(exception, request, response);
		});
		
		exception(ResourceException.class, (exception, request, response) -> {
			resourceException(exception, request, response);
		});
		
		exception(ZoneRulesException.class, (exception, request, response) -> {
			zoneRulesException(exception, request, response);
		});
	}
    
    private static void serviceException(ServiceException exception, Request request, Response response) {
		Map<String, Object> model = getModel();
		model.put("errorMessage", exception.getMessage());

		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(AuthenticationController.class)
				.model(model)
				.templateName(Templates.ERROR)
				.build();

		response.status(HttpStatus.BAD_REQUEST_400);
		response.body(processTemplate(templateProcessRequest));
    };
    
    private static void resourceException(ResourceException exception, Request request, Response response) {	
		Map<String, Object> model = getModel();
		model.put("errorMessage", exception.getMessage());

		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(AuthenticationController.class)
				.model(model)
				.templateName(Templates.ERROR)
				.build();

		response.status(HttpStatus.BAD_REQUEST_400);
		response.body(processTemplate(templateProcessRequest));
    };
    
    private static void zoneRulesException(ZoneRulesException exception, Request request, Response response) {
    	Map<String, Object> model = getModel();
		model.put("errorMessage", exception.getMessage());

		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(AuthenticationController.class)
				.model(model)
				.templateName(Templates.ERROR)
				.build();
		
		response.status(HttpStatus.BAD_REQUEST_400);
		response.body(processTemplate(templateProcessRequest));
    };
    
    private static void notFoundExeption(NotFoundException exception, Request request, Response response) {
		Map<String, Object> model = getModel();
		model.put("errorMessage", MessageProvider.getMessage(request.attribute(RequestAttributes.LOCALE), "page.not.found"));

		ProcessTemplateRequest processTemplateRequest = ProcessTemplateRequest.builder()
				.controllerClass(AuthenticationController.class)
				.model(model)
				.templateName(Templates.NOT_FOUND)
				.build();

		response.status(HttpStatus.NOT_FOUND_404);
		response.body(processTemplate(processTemplateRequest));
    };
}
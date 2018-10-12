package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.util.Map;

import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import spark.Request;
import spark.Response;

public class AdministrationController extends BaseController {
	
	public static void configureRoutes() {
		get(Path.Route.ADMINISTRATION, (request, response) -> viewStartPage(request, response));
	}

	private static String viewStartPage(Request request, Response response) {
		
		String organizationId = getIdentity(request).getOrganization().getId();
				
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
    	
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(StartController.class)
				.model(model)
				.templateName(Templates.ADMINISTRATION)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
}
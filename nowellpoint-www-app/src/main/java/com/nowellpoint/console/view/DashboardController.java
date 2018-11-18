package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.util.Map;

import com.nowellpoint.console.model.Dashboard;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;

import spark.Request;
import spark.Response;

public class DashboardController extends BaseController {
	
	public static void configureRoutes() {

		get(Path.Route.DASHBOARD, (request, response) 
				-> DashboardController.showDashboard(request, response));
	}
	
	public static String showDashboard(Request request, Response response) {
		
		Dashboard dashboard = ServiceClient.getInstance()
				.organization()
				.get(getIdentity(request).getOrganization().getId())
				.getDashboard();
		
		Map<String, Object> model = getModel();
		model.put("dashboard", dashboard);
		
		ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(DashboardController.class)
				.model(model)
				.templateName(Templates.DASHBOARD)
				.build();
		
		return processTemplate(templateProcessRequest);
	};
}
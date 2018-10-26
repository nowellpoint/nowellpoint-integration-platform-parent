package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;

import spark.Request;
import spark.Response;

public class StartController extends BaseController {
	
	public static void configureRoutes() {
		get(Path.Route.START, (request, response) -> viewStartPage(request, response));
	}

	private static String viewStartPage(Request request, Response response) {
		
		String organizationId = getIdentity(request).getOrganization().getId();
				
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);

		String authUrl = new StringBuilder(System.getenv("SALESFORCE_AUTHORIZE_URI"))
				.append("?response_type=code")
				.append("&client_id=")
				.append(System.getenv("SALESFORCE_CLIENT_ID"))
				.append("&redirect_uri=")
				.append(System.getenv("SALESFORCE_REDIRECT_URI"))
				.append("&scope=refresh_token")
				.append("&prompt=login")
				.append("&display=popup")
				.append("&state=")
				.append(organization.getId())
				.toString();	
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		model.put("SALESFORCE_AUTHORIZE_URI", authUrl);
    	
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(StartController.class)
				.model(model)
				.templateName(Templates.START)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	
}
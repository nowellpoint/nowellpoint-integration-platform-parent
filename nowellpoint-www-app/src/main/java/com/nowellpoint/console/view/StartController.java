package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.Templates;

import spark.Request;
import spark.Response;

public class StartController extends BaseController {
	
	public static void configureRoutes() {
		get(Path.Route.START, (request, response) 
				-> viewStartPage(request, response));
		
		get(Path.Route.ORGANIZATION_CONNECTED_USER, (request, response) 
				-> changeConnectedUser(request, response));
	}

	private static String viewStartPage(Request request, Response response) {
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(getIdentity(request).getOrganization().getId());
		
		Map<String,Object> model = getModel();
		model.put("organization", organization);
		model.put("CHANGE_CONNECTED_USER_URI", Path.Route.ORGANIZATION_CONNECTED_USER.replace(":id", organization.getId()));
    	
    	ProcessTemplateRequest templateProcessRequest = ProcessTemplateRequest.builder()
				.controllerClass(StartController.class)
				.model(model)
				.templateName(Templates.START)
				.build();
		
		return processTemplate(templateProcessRequest);
	};	

	private static String changeConnectedUser(Request request, Response response) {

		String organizationId = request.params(":id");

		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(organizationId);
		
		try {
			
			String authUrl = new StringBuilder(EnvironmentVariables.getSalesforceAuthorizeUri())
					.append("?response_type=code")
					.append("&client_id=")
					.append(EnvironmentVariables.getSalesforceClientId())
					.append("&redirect_uri=")
					.append(EnvironmentVariables.getSalesforceCallbackUri())
					.append("&scope=")
					.append(URLEncoder.encode("refresh_token api", "UTF-8"))
					.append("&prompt=login")
					.append("&display=popup")
					.append("&state=")
					.append(organization.getId())
					.toString();
			
			response.redirect(authUrl);	
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "";
	};
}
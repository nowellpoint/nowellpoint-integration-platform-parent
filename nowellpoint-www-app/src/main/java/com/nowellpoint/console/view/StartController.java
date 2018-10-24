package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.ProcessTemplateRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

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

		String authUrl = null;

		try {

			authUrl = new StringBuilder(System.getenv("SALESFORCE_AUTHORIZE_URI"))
				.append("?response_type=token")
				.append("&client_id=")
				.append(System.getenv("SALESFORCE_CLIENT_ID"))
				.append("&client_secret=")
				.append(System.getenv("SALESFORCE_CLIENT_SECRET"))
				.append("&redirect_uri=")
				.append(System.getenv("SALESFORCE_REDIRECT_URI"))
				.append("&scope=")
				.append(URLEncoder.encode("api refresh_token", "UTF-8"))
				.append("&display=popup")
				.append("&state=")
				.append(organization.getId())
				.toString();	
				
			System.out.println(authUrl);

		}	catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	

				
		
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
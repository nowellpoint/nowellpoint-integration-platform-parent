package com.nowellpoint.www.app.view;

import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.Dashboard;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.TemplateBuilder;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class DashboardController extends AbstractStaticController {
	
	public static class Template {
		public static final String DASHBOARD = secureTemplate("dashboard.html");
	}
	
	public static String showDashboard(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		Dashboard dashboard = NowellpointClient.defaultClient(token)
				.dashboard()
				.get();
		
		Map<String, Object> model = getModel();
		model.put("dashboard", dashboard);
		
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(DashboardController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.DASHBOARD)
				.timeZone(getTimeZone(request))
				.build();
	};
}
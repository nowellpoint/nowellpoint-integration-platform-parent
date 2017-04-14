package com.nowellpoint.www.app.view;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nowellpoint.client.NowellpointClientOrig;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class DashboardController extends AbstractStaticController {
	
	public static class Template {
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard.html");
	}
	
	public static String showDashboard(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		Map<String, Object> model = getModel();
    	return render(DashboardController.class, configuration, request, response, model, Template.DASHBOARD);
	};
}
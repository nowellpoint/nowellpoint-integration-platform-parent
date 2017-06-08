package com.nowellpoint.www.app.view;

import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.JobList;
import com.nowellpoint.client.model.SalesforceConnectorList;
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
		
		SalesforceConnectorList salesforceConnectors = NowellpointClient.defaultClient(token)
				.salesforceConnector()
				.getSalesforceConnectors();    	
		
		JobList jobList = NowellpointClient.defaultClient(token)
				.job()
				.getJobs();
		
		Map<String, Object> model = getModel();
		model.put("salesforceConnectorsList", salesforceConnectors.getItems());
		model.put("jobList", jobList.getItems());
		
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
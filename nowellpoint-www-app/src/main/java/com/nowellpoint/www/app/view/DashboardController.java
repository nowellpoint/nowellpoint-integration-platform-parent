package com.nowellpoint.www.app.view;

import java.util.Map;

import com.nowellpoint.client.NowellpointClientOrig;
import com.nowellpoint.client.model.JobList;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.Token;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class DashboardController extends AbstractStaticController {
	
	public static class Template {
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard.html");
	}
	
	public static String showDashboard(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		SalesforceConnectorList salesforceConnectors = new NowellpointClientOrig(token)
				.salesforceConnector()
				.getSalesforceConnectors();    	
		
		JobList jobList = new NowellpointClientOrig(token)
				.job()
				.getJobs();
		
		Map<String, Object> model = getModel();
		model.put("salesforceConnectorsList", salesforceConnectors.getItems());
		model.put("jobList", jobList.getItems());
		
		
		
    	return render(DashboardController.class, configuration, request, response, model, Template.DASHBOARD);
	};
}
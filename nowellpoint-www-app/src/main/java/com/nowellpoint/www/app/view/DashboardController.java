package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.List;
import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class DashboardController extends AbstractController {
	
	public static class Template {
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard.html");
	}
	
	public DashboardController() {     
		super(DashboardController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.START, (request, response) -> showStartPage(configuration, request, response));
        get(Path.Route.DASHBOARD, (request, response) -> showDashboard(configuration, request, response));
	}
	
	private String showStartPage(Configuration configuration, Request request, Response response) {
		
    	Map<String,Object> model = getModel();
    	model.put("account", getAccount(request));
    	
    	return render(configuration, request, response, model, Path.Template.START);
	};
	
	private String showDashboard(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		GetResult<List<ScheduledJob>> getResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.getScheduledJobs();
		
		Map<String, Object> model = getModel();
		model.put("scheduledJobList", getResult.getTarget());
		model.put("scheduledJobPath", Path.Route.SCHEDULED_JOBS_LIST);
    	model.put("account", getAccount(request));
    	return render(configuration, request, response, model, Template.DASHBOARD);
	};
}
package com.nowellpoint.www.app.view;

import java.util.List;
import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.idp.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class DashboardController extends AbstractController {
	
	public DashboardController(Configuration configuration) {     
		super(DashboardController.class, configuration);
	}
	
	public Route showStartPage = (Request request, Response response) -> {
    	Map<String,Object> model = getModel();
    	model.put("account", getAccount(request));
    	return render(request, model, Path.Template.START);
	};
	
	public Route showDashboard = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		List<ScheduledJob> scheduledJobs = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.getScheduledJobs();
		
		Map<String, Object> model = getModel();
		model.put("scheduledJobList", scheduledJobs);
		model.put("scheduledJobPath", Path.Route.SCHEDULED_JOBS_LIST);
    	model.put("account", getAccount(request));
    	return render(request, model, Path.Template.DASHBOARD);
	};
}
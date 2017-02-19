package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.JobSchedule;
import com.nowellpoint.client.model.JobScheduleList;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class DashboardController extends AbstractStaticController {
	
	public static class Template {
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard.html");
	}
	
	public static void configureRoutes(Configuration configuration) {
        get(Path.Route.DASHBOARD, (request, response) -> showDashboard(configuration, request, response));
	}
	
	private static String showDashboard(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		JobScheduleList list = new NowellpointClient(token)
				.jobSchedule()
				.getJobSchedules();
		
		List<JobSchedule> jobSchedules = list.getItems()
				.stream()
				.filter(job -> job.getConnector().getInstance().getKey() != null)
				.collect(Collectors.toList());
		
		Map<String, Object> model = getModel();
		model.put("scheduledJobList", jobSchedules);
		model.put("scheduledJobPath", Path.Route.SCHEDULED_JOBS_LIST);
    	return render(DashboardController.class, configuration, request, response, model, Template.DASHBOARD);
	};
}
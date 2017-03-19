package com.nowellpoint.www.app.view;

import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.JobList;
import com.nowellpoint.client.model.Token;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class JobController extends AbstractStaticController {
	
	public static class Template {
		public static final String JOBS_LIST = String.format(APPLICATION_CONTEXT, "jobs-list.html");
	}
	
	public static String listJobs(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		JobList jobList = new NowellpointClient(token)
				.job()
				.getJobs();
		
		Map<String, Object> model = getModel();
    	model.put("jobList", jobList.getItems());
    	
    	return render(JobController.class, configuration, request, response, model, Template.JOBS_LIST);
	}
	
	public static String viewJob(Configuration configuration, Request request, Response response) {
		return null;
	}
	
	public static String createJob(Configuration configuration, Request request, Response response) {
		return null;
	}
	
	public static String updateJob(Configuration configuration, Request request, Response response) {
		return null;
	}
}
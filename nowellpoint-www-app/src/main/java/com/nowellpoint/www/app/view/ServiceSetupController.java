package com.nowellpoint.www.app.view;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.JobSpecification;
import com.nowellpoint.client.model.JobSpecificationList;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class ServiceSetupController extends AbstractStaticController {
	
	public static class Template {
		public static final String SERVICE_METADATA_BACKUP_SETUP = String.format(APPLICATION_CONTEXT, "service-metadata-backup-setup.html");
	}
	
	public static String metadataBackup(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		JobSpecificationList list = new NowellpointClient(token)
				.jobSpecification()
				.getJobSpecifications();
		
		List<JobSpecification> jobSpecifications = list.getItems()
				.stream()
				.filter(job -> job.getConnector().getInstance().getKey() != null)
				.sorted((job1, job2) -> job1.getConnector().getInstance().getName().compareTo(job2.getConnector().getInstance().getName()))
				.collect(Collectors.toList());
		
		Map<String, Object> model = getModel();
		model.put("jobSpecificationList", jobSpecifications);
		model.put("scheduledJobPath", Path.Route.JOB_SPECIFICATION_LIST);
    	return render(ServiceSetupController.class, configuration, request, response, model, Template.SERVICE_METADATA_BACKUP_SETUP);
	};
}
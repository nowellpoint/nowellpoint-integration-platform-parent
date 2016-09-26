package com.nowellpoint.api.job;

import org.jboss.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import static com.mongodb.client.model.Filters.eq;

import com.nowellpoint.api.model.document.ScheduledJobRunDetail;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.mongodb.document.MongoDocumentService;

public class SalesforceMetadataBackupListener implements JobListener {
	
	private static final Logger LOG = Logger.getLogger(SalesforceMetadataBackupListener.class);
	private static final String LISTENER_NAME = SalesforceMetadataBackupListener.class.getName();
	
	private ScheduledJobRunDetailService service = new ScheduledJobRunDetailService(); 

	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {

		String jobName = context.getJobDetail().getKey().getName();
		String groupName = context.getJobDetail().getKey().getGroup();
		
		LOG.info(context.getFireInstanceId());
		
		LOG.info(String.format("JobToBeExecuted: %s", jobName));
		
		ScheduledJobRunDetail scheduledJobRunDetail = new ScheduledJobRunDetail();
		scheduledJobRunDetail.setFireInstanceId(context.getFireInstanceId());
		scheduledJobRunDetail.setJobName(jobName);
		scheduledJobRunDetail.setGroupName(groupName);
		scheduledJobRunDetail.setCreatedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		scheduledJobRunDetail.setLastModifiedById(System.getProperty(Properties.DEFAULT_SUBJECT));

		service.create(scheduledJobRunDetail);
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		LOG.error("jobExecutionVetoed");
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		
		String jobName = context.getJobDetail().getKey().getName();
		
		LOG.info(context.getFireInstanceId());
		
		LOG.info(String.format("JobWasExecuted: %s", jobName));
		
		ScheduledJobRunDetail scheduledJobRunDetail = null;
		try {
			scheduledJobRunDetail = service.findByFireInstanceId(context.getFireInstanceId());
		} catch (DocumentNotFoundException e) {
			LOG.error(e);
			return;
		}
		
		scheduledJobRunDetail.setJobRunTime(context.getJobRunTime());
		scheduledJobRunDetail.setLastModifiedById(System.getProperty(Properties.DEFAULT_SUBJECT));

		if (jobException != null) {
			scheduledJobRunDetail.setStatus("Error");
			scheduledJobRunDetail.setErrorMessage(jobException.getMessage());
		} else {
			scheduledJobRunDetail.setStatus("Success");
		}
		
		service.replace(scheduledJobRunDetail);
	}
}

class ScheduledJobRunDetailService extends MongoDocumentService<ScheduledJobRunDetail> {

	public ScheduledJobRunDetailService() {
		super(ScheduledJobRunDetail.class);
	}
	
	public ScheduledJobRunDetail findByFireInstanceId(String fireInstanceId) {
		return super.findOne( eq ( "fireInstanceId", fireInstanceId ) );
	}
	
	public void create(ScheduledJobRunDetail scheduledJobRunDetail) {
		super.create(scheduledJobRunDetail);
	}
	
	public void replace(ScheduledJobRunDetail scheduledJobRunDetail) {
		super.replace(eq ( "_id", scheduledJobRunDetail.getId() ), scheduledJobRunDetail);
	}
}
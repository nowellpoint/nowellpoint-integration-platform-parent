package com.nowellpoint.api.job;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.data.LogManager;

public class SalesforceMetadataBackupListener implements JobListener {
	
	private static final Logger LOG = Logger.getLogger(SalesforceMetadataBackupListener.class);
	private static final String LISTENER_NAME = SalesforceMetadataBackupListener.class.getName();

	@Override
	public String getName() {
		return LISTENER_NAME;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		LOG.info("Firing job: " + context.getFireInstanceId());
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		LOG.error("jobExecutionVetoed");
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			LOG.error(e);
		}
		
		ObjectNode node = JsonNodeFactory.instance.objectNode()
				.put("hostname", hostname)
				.put("firstInstanceId", context.getFireInstanceId())
				.put("fireTime", context.getFireTime().getTime())
				.put("jobRunTime", context.getJobRunTime())
				.put("jobName", context.getJobDetail().getKey().getName())
				.put("groupName", context.getJobDetail().getKey().getGroup())
				.put("result", context.getResult() != null ? context.getResult().toString() : null)
				.put("exception", jobException != null ? jobException.getMessage() : null);
		
		LogManager.writeLogEntry("job", node.toString());
	}
}
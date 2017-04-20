package com.nowellpoint.api.rest.service;

import java.io.IOException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.JobOutput;
import com.nowellpoint.api.rest.domain.Schedule;
import com.nowellpoint.api.service.JobService;

public class JobServiceImpl extends AbstractJobService implements JobService {

	@Override
	public JobList findAllByOwner(String ownerId) {
		return super.findAllByOwner(ownerId);
	}
	
	@Override
	public JobList findAllScheduled() {
		return super.findAllScheduled();
	}

	@Override
	public Job findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public JobExecution findByFireInstanceId(String id, String fireInstanceId) {
		Job job = findById(id);
		return job.getJobExecution(fireInstanceId);
	}
	
	@Override
	public String getOutputFile(String id, String fireInstanceId, String filename) throws IOException {
		Job job = findById(id);
		JobExecution jobExecution = job.getJobExecution(fireInstanceId);
		JobOutput jobOutput = jobExecution.getJobOutput(filename);
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(jobOutput.getBucket(), jobOutput.getKey());
    	
    	S3Object s3Object = s3client.getObject(getObjectRequest);
    	
    	return IOUtils.toString(s3Object.getObjectContent());
	}

	@Override
	public void createJob(Job job) {
		job.setStatus(Job.Statuses.SCHEDULED);
		super.create(job);
	}

	@Override
	public void updateJob(Job job) {
		super.update(job);
	}
	
	@Override
	public void runJob(Job job) {
		super.run(job);
	}
}
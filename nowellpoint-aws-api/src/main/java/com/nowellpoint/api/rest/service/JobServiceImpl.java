package com.nowellpoint.api.rest.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.event.Observes;
import javax.validation.ValidationException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.JobOutput;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.JobService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.util.Assert;

public class JobServiceImpl extends AbstractJobService implements JobService {
	
	protected final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());

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
	public JobList queryBySource(String sourceId) {
		return super.query( Filters.eq ( "source.id", sourceId ) );
	}
	
	@Override
	public String getOutputFile(String id, String filename) throws IOException {
		Job job = findById(id);
		JobOutput jobOutput = job.getJobOutput(filename);
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(jobOutput.getBucket(), jobOutput.getKey());
    	
    	S3Object s3Object = s3client.getObject(getObjectRequest);
    	
    	return IOUtils.toString(s3Object.getObjectContent());
	}
	
	@Override
	public Job createJob(@Observes CreateJobRequest jobRequest) {
		
		List<String> errors = new ArrayList<>();
		
		if (Assert.isNull(jobRequest.getSource())) {
			errors.add("Missing Source. Create Job must include a Source system");
		}
		
		if (Assert.isNull(jobRequest.getJobType())) {
			errors.add("Missing Job Type. Create Job must include a Job Type");
		}
		
		if (Assert.isNull(jobRequest.getSchedule()) && ! jobRequest.getScheduleOption().isPresent()) {
			errors.add("Missing scheduleOption parameter. Must provide a value of RUN_WHEN_SUBMITTED, RUN_ONCE, RUN_ON_SCHEDULE or RUN_ON_SPECIFIC_DAYS");
		}
		
		if (! errors.isEmpty()) {
			String errorMessage = errors.stream().collect(Collectors.joining ("\n"));
			throw new ValidationException(errorMessage);
		}
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());

		Job job = Job.of(jobRequest, userInfo);
		
		super.create(job);
		
		return job;
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
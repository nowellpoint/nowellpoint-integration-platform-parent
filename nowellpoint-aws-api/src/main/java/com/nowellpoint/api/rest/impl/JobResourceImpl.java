package com.nowellpoint.api.rest.impl;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.JobResource;
import com.nowellpoint.api.rest.SalesforceConnectorResource;
import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.Meta;
import com.nowellpoint.api.rest.domain.RunWhenSubmitted;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.Schedule;
import com.nowellpoint.api.rest.domain.Source;
import com.nowellpoint.api.service.CommunicationService;
import com.nowellpoint.api.service.JobService;
import com.nowellpoint.api.service.JobTypeService;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.util.Assert;

public class JobResourceImpl implements JobResource {
	
	protected final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	
	@Inject
	private JobService jobService;
	
	@Inject
	private JobTypeService jobTypeService;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Inject
	private CommunicationService communicationService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;

	@Override
	public Response findAllByOwner() {
		JobList jobList = jobService.findAllByOwner(securityContext.getUserPrincipal().getName());
		return Response.ok(jobList).build();
	}
	
	@Override
	public Response getJobExecution(String id, String fireInstanceId) {
		JobExecution jobExecution = jobService.findByFireInstanceId(id, fireInstanceId);
		return Response.ok(jobExecution).build();
	}
	
	@Override
	public Response getOutputFile(String id, String filename) {
		
		String content;
		try {
			content = jobService.getOutputFile(id, filename);
		} catch (IOException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok()
				.header("Content-Disposition", String.format("attachment; filename=\"%s.json\"", filename))
				.entity(content)
				.build();
	}
	
	@Override
	public Response createJob(
			String connectorId,
			String jobTypeId,
			String notificationEmail,
			String slackWebhookUrl,
			String scheduleOption,
			String runAt,
			String dayOfMonth,
			String dayOfWeek,
			String description,
			String hours,
			String endAt,
			String minutes,
			String month,
			String seconds,
			String startAt,
			String timeZone,
			String timeUnit,
			String timeInterval,
			String year) {
		
		JobType jobType = jobTypeService.findById(jobTypeId);
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById(connectorId);
		
		Source source = Source.of(salesforceConnector);
		
		CreateJobRequest createJobRequest = CreateJobRequest.builder()
				.dayOfMonth(dayOfMonth)
				.dayOfWeek(dayOfWeek)
				.description(description)
				.endAt(endAt)
				.hours(hours)
				.minutes(minutes)
				.month(month)
				.notificationEmail(notificationEmail)
				.runAt(runAt)
				.scheduleOption(scheduleOption)
				.seconds(seconds)
				.slackWebhookUrl(slackWebhookUrl)
				.startAt(startAt)
				.timeInterval(timeInterval)
				.timeUnit(timeUnit)
				.year(year)
				.jobType(jobType)
				.source(source)
				.build();
		
		Job job = jobService.createJob(createJobRequest);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(JobResource.class)
				.path("/{id}")
				.build(job.getId());
		
		return Response.created(uri)
				.entity(job)
				.build();
	}
	
	@Override
	public Response updateJob(String id,
			String dayOfMonth,
			String dayOfWeek,
			String description,
			String runAt,
			String hours,
			String jobName,
			String end,
			String minutes,
			String month,
			String notificationEmail,
			String scheduleOption,
			String seconds,
			String start,
			String timeZone,
			String year) {
		return null;
	}

	@Override
	public Response getJob(String id) {
		
		Job job = jobService.findById(id);
		
		if (job == null){
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", Job.class.getSimpleName(), id ) );
		}
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceConnectorResource.class)
				.path("/{id}")
				.build(job.getId());
		
		Meta meta = new Meta();
		meta.setHref(uri.toString());
		
		job.setMeta(meta);
		
		return Response.ok(job).build();
	}

	@Override
	public Response invokeAction(String id, String action) {
		
		Job job = jobService.findById(id);
		
		if ("run".equals(action)) {
			job.setSchedule(Schedule.of(RunWhenSubmitted.builder().build()));
			jobService.runJob(job);
		} else if ("test-webhook-url".equals(action)) {
			if (Assert.isNotNull(job.getSlackWebhookUrl())) {
				communicationService.sendMessage(job.getSlackWebhookUrl(), "Nowellpoint Notification Service", "Test to ensure external communication service functions as expected");
			}
		}
		
		return Response.ok()
				.entity(job)
				.build();
	}

}
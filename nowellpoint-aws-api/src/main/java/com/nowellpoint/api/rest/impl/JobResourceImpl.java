/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
import com.nowellpoint.api.rest.domain.UpdateJobRequest;
import com.nowellpoint.api.service.JobService;
import com.nowellpoint.api.service.JobTypeService;
import com.nowellpoint.api.service.SalesforceConnectorService;

public class JobResourceImpl implements JobResource {
	
	protected final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	
	@Inject
	private JobService jobService;
	
	@Inject
	private JobTypeService jobTypeService;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
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
		
		CreateJobRequest jobRequest = CreateJobRequest.builder()
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
				.timeZone(timeZone)
				.year(year)
				.jobType(jobType)
				.source(source)
				.build();
		
		Job job = jobService.createJob(jobRequest);
		
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
		
		UpdateJobRequest jobRequest = UpdateJobRequest.builder()
				.id(id)
				.description(description)
				.notificationEmail(notificationEmail)
				.slackWebhookUrl(slackWebhookUrl)
				//.dayOfMonth(dayOfMonth)
				//.dayOfWeek(dayOfWeek)
				//.endAt(endAt)
				//.hours(hours)
				//.minutes(minutes)
				//.month(month)
				//.runAt(runAt)
				//.scheduleOption(scheduleOption)
				//.seconds(seconds)
				//.startAt(startAt)
				//.timeInterval(timeInterval)
				//.timeUnit(timeUnit)
				//.timeZone(timeZone)
				//.year(year)
				.build();
		
		Job job = jobService.updateJob(jobRequest);
		
		return Response.ok(job).build();
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
		
		if ("submit".equals(action)) {
			job.setSchedule(Schedule.of(RunWhenSubmitted.builder().build()));
			jobService.submitJob(job);
		} else if ("test-webhook-url".equals(action)) {
			jobService.sendSlackTestMessage(job);
		} else if ("stop".equals(action)) {
			jobService.stopJob(job);
		} else if ("terminate".equals(action)) {
			jobService.terminateJob(job);
		}
		
		return Response.ok()
				.entity(job)
				.build();
	}

}
package com.nowellpoint.api.rest.impl;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.nowellpoint.api.rest.JobResource;
import com.nowellpoint.api.rest.domain.Error;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.Schedule;
import com.nowellpoint.api.rest.domain.Source;
import com.nowellpoint.api.rest.domain.UserInfo;
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
	public Response createJob(
			String connectorId,
			String jobTypeId,
			String notificationEmail,
			String scheduleOption,
			String runAt,
			String dayOfMonth,
			String dayOfWeek,
			String description,
			String hours,
			String end,
			String minutes,
			String month,
			String seconds,
			String start,
			String timeZone,
			String year) {
		
		List<String> errors = new ArrayList<>();
		
		Date startDate = null;
		
		try {
			startDate = dateTimeFormat.parse(start);
		} catch (ParseException e) {
			errors.add("Invalid date format for field: start. Use the following format: yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		}
		
		Date endDate = null;
		
		try {
			endDate = dateTimeFormat.parse(end);
		} catch (ParseException e) {
			errors.add("Invalid date format for parameter: end. Use the following format: yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		}
		
		Date runDate = null;
		
		try {
			runDate = dateTimeFormat.parse(runAt);
		} catch (ParseException e) {
			errors.add("Invalid date format for parameter: run at. Use the following format: yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		}
		
		if (Assert.isNullOrEmpty(connectorId)) {
			errors.add("Missing connectorId parameter. Must provide a valid Salesforce Connector Id");
		}
		
		if (Assert.isNullOrEmpty(jobTypeId)) {
			errors.add("Missing jobTypeId parameter. Must provide a valid Job Type Id");
		}
		
		if (Assert.isNullOrEmpty(scheduleOption)) {
			errors.add("Missing scheduleOption parameter. Must provide a value of RUN_WHEN_SUBMITTED, ONCE, SCHEDULE or SPECIFIC_DAYS");
		}
		
		if (! errors.isEmpty()) {
			Error error = new Error(3000, errors.toArray(new String[errors.size()]));
			ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
			builder.entity(error);
			return builder.build();
		}

		JobType jobType = jobTypeService.findById(jobTypeId);
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById(connectorId);
		
		UserInfo user = UserInfo.of(securityContext.getUserPrincipal().getName());
		
		Schedule schedule = Schedule.of(
				runDate,
				startDate, 
				endDate, 
				timeZone, 
				seconds, 
				minutes, 
				hours, 
				dayOfMonth, 
				month, 
				dayOfWeek, 
				year);
		
		Source source = Source.of(salesforceConnector);

		Job job = Job.of(
				source,
				jobType,
				schedule,
				user,
				description, 
				notificationEmail, 
				scheduleOption);
		
		jobService.createJob(job);
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response invokeAction(String id, String action) {
		// TODO Auto-generated method stub
		return null;
	}

}
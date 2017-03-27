package com.nowellpoint.api.rest.impl;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.JobResource;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.JobService;

public class JobResourceImpl implements JobResource {
	
	protected final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	
	@Inject
	private JobService jobService;
	
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
			String dayOfMonth,
			String dayOfWeek,
			String description,
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
		
		Date endDate = null;
		Date startDate = null;
		
		try {
			endDate = dateTimeFormat.parse(end);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date format for field: end. Use the following format: yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		}
		
		try {
			startDate = dateTimeFormat.parse(start);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date format for field: start. Use the following format: yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		}
		
		Job job = Job.of(
				UserInfo.of(securityContext.getUserPrincipal().getName()),
				dayOfMonth, 
				dayOfWeek, 
				description, 
				hours, 
				jobName, 
				endDate, 
				minutes, 
				month, 
				notificationEmail, 
				scheduleOption,
				seconds, 
				startDate, 
				timeZone, 
				year);
		
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
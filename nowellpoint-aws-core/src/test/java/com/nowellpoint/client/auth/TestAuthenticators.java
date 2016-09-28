package com.nowellpoint.client.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.junit.Test;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.CreateScheduledJobRequest;
import com.nowellpoint.client.model.DeleteScheduledJobRequest;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.UpdateScheduledJobRequest;

public class TestAuthenticators {
	
	@Test
	public void testPasswordGrantAuthentication() {
		
		try {			
			NowellpointClient client = new NowellpointClient(new EnvironmentVariablesCredentials());
			
			CreateScheduledJobRequest createScheduledJobRequest = new CreateScheduledJobRequest()
					.withConnectorId("57df3e22019362745462305c")
					.withDescription("My scheduled job description")
					.withJobTypeId("57d7e6ccb55f01245754d0af")
					.withScheduleDate(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));
			
			ScheduledJob scheduledJob = client.getScheduledJobResource()
					.createScheduledJob(createScheduledJobRequest)
					.getScheduledJob();
			
			assertNotNull(scheduledJob);
			assertNotNull(scheduledJob.getId());
			
			System.out.println(scheduledJob.getId());
			
			assertEquals(scheduledJob.getEnvironmentKey(), "40799dbbe97b479baa0772eb4e6ba8cb");
			
			scheduledJob = client.getScheduledJobResource().getScheduledJob(scheduledJob.getId());
			
			assertNotNull(scheduledJob);
			
			UpdateScheduledJobRequest updateScheduledJobRequest = new UpdateScheduledJobRequest()
					.withId(scheduledJob.getId())
					.withConnectorId("57df3e22019362745462305c")
					.withDescription("My scheduled job description with update")
					.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse("2016-10-01".concat("T").concat("16:00:00")));
			
			scheduledJob = client.getScheduledJobResource()
					.updateScheduledJob(updateScheduledJobRequest)
					.getScheduledJob();
			
			assertNotNull(scheduledJob);
			assertEquals(scheduledJob.getDescription(), "My scheduled job description with update");
			
			System.out.println(scheduledJob.getId());
			
			DeleteScheduledJobRequest deleteScheduledJobRequest = new DeleteScheduledJobRequest()
					.withId(scheduledJob.getId());
			
			client.getScheduledJobResource().deleteScheduledJob(deleteScheduledJobRequest);
			
			client.logout();
			
		} catch (OauthException e) {
			System.out.println(e.getCode());
			System.out.println(e.getMessage());
		} catch (NowellpointServiceException e) {
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
package com.nowellpoint.client.auth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.junit.Test;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.CreateScheduledJobRequest;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.ScheduledJob;

import io.jsonwebtoken.lang.Assert;

public class TestAuthenticators {
	
	@Test
	public void testPasswordGrantAuthentication() {
		
		BasicCredentials credentials = new BasicCredentials(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"));
		
		try {
			NowellpointClient client = new NowellpointClient(credentials);
			
			CreateScheduledJobRequest createScheduledJobRequest = new CreateScheduledJobRequest()
					.withConnectorId("57df3e22019362745462305c")
					.withDescription("My scheduled job description")
					.withEnvironmentKey("40799dbbe97b479baa0772eb4e6ba8cb")
					.withJobTypeId("57d7e6ccb55f01245754d0af")
					.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse("2016-09-28".concat("T").concat("15:00:00")));
			
			ScheduledJob scheduledJob = client.getScheduledJobResource()
					.createScheduledJob(createScheduledJobRequest);
			
			Assert.notNull(scheduledJob.getId());
			
			System.out.println(scheduledJob.getId());
			
			client.getScheduledJobResource().deleteScheduledJob(scheduledJob.getId());
			
			client.logout();
			
		} catch (OauthException e) {
			System.out.println(e.getCode());
			System.out.println(e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NowellpointServiceException e) {
			System.out.println(e.getMessage());
		}
	}
}
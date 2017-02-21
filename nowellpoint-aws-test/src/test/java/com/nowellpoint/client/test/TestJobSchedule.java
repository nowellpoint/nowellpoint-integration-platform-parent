package com.nowellpoint.client.test;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.JobSchedule;
import com.nowellpoint.client.model.JobScheduleRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

import org.junit.Assert;

public class TestJobSchedule {
	
	private static Token token;

	@BeforeClass
	public static void authenticate() {
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		token = response.getToken();
		
		System.out.println(token.getAccessToken());
	}
	
	@Test
	public void testCreateUpdateJobSchedule() {
		
		JobScheduleRequest createRequest = new JobScheduleRequest()
				.withJobTypeId("57d7e6ccb55f01245754d0af")
				.withConnectorId("58a3a9bc1ed1fec30b6a46fd")
				.withInstanceKey("8ff183a8339f4810a10d832248efe7db")
				.withStart(new Date())
				.withSeconds("0")
				.withMinutes("60")
				.withHours("*")
				.withDayOfMonth("*")
				.withMonth("*")
				.withDayOfWeek("*")
				.withYear("*");
		
		CreateResult<JobSchedule> createResult = new NowellpointClient(token)
				.jobSchedule()
				.create(createRequest);
		
		System.out.println(createResult.getErrorMessage());
		Assert.assertTrue(createResult.isSuccess());
		Assert.assertEquals(createResult.getTarget().getNotificationEmail(), "john.d.herson@gmail.com");
		
		if (! createResult.isSuccess()) {
			System.out.println(createResult.getErrorMessage());
		}
		
		JobSchedule jobSchedule = new NowellpointClient(token)
				.jobSchedule()
				.get(createResult.getTarget().getId());
		
		Assert.assertNotNull(jobSchedule);
		
		UpdateResult<JobSchedule> startJobResult = new NowellpointClient(token)
				.jobSchedule()
				.start(jobSchedule.getId());
		
		System.out.println(startJobResult.isSuccess());
		
		JobScheduleRequest updateRequest = new JobScheduleRequest()
				.withId(jobSchedule.getId())
				.withNotificationEmail("john.d.herson@gmail.com")
				.withDescription("Here is my new description")
				.withStart(new Date())
				.withSeconds(jobSchedule.getSeconds())
				.withMinutes(jobSchedule.getMinutes())
				.withHours(jobSchedule.getHours())
				.withDayOfMonth(jobSchedule.getDayOfMonth())
				.withMonth(jobSchedule.getMonth())
				.withDayOfWeek(jobSchedule.getDayOfWeek())
				.withYear(jobSchedule.getYear());

		UpdateResult<JobSchedule> updateResult = new NowellpointClient(token)
				.jobSchedule() 
				.update(updateRequest);
		
		Assert.assertTrue(updateResult.isSuccess());
		
		if (! updateResult.isSuccess()) {
			System.out.println(updateResult.getErrorMessage());
		}
		
	}
	
	@AfterClass
	public static void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(token)
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
}
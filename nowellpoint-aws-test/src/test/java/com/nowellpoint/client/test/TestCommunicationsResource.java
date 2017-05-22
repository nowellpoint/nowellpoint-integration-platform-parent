package com.nowellpoint.client.test;

import org.junit.Test;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.model.Job;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.util.Assert;

public class TestCommunicationsResource {

	@Test
	public void testSendTestMessage() {
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.setEnvironment(Environment.SANDBOX)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = response.getToken();
		
		UpdateResult<Job> updateResult = NowellpointClient.defaultClient(token)
				.job()
				.testWebHookUrl("");
		
	}
}
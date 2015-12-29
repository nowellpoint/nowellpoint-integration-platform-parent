package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.logging.Logger;

import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.idp.CreateAccountRequest;
import com.nowellpoint.aws.model.idp.CreateAccountResponse;

public class AccountEventHandler implements AbstractEventHandler {
	
	private static final Logger log = Logger.getLogger(AccountEventHandler.class.getName());

	@Override
	public String process(String payload) throws IOException {
		
		log.info("starting LeadEventHandler");
		
		//
		// parse the event payload
		//
		
		Account account = objectMapper.readValue(payload, Account.class);
		
		//
		// setup IdentityProviderClient
		//
		
		final IdentityProviderClient identityProviderClient = new IdentityProviderClient();
		
		//
		// build the CreateAccountRequest
		//
		
		CreateAccountRequest createAccountRequest = new CreateAccountRequest().withAccount(account)
				.withApiKeyId(Configuration.getStormpathApiKeyId())
				.withApiKeySecret(Configuration.getStormpathApiKeySecret());
		
		//
		// execute the CreateAcountRequest
		//
		
		CreateAccountResponse createAccountResponse = identityProviderClient.account(createAccountRequest);
		
		//
		// throw exception for any issue with the identity provider
		//
		
		if (createAccountResponse.getStatusCode() != 201) {
			throw new IOException(createAccountResponse.getErrorMessage());
		}
		
		log.info(createAccountResponse.getAccount().getHref());
		
		//
		//
		//
		
		return createAccountResponse.getAccount().getHref();
	}
}
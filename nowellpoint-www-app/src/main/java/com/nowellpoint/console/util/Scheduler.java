package com.nowellpoint.console.util;

import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;

import com.nowellpoint.util.SecretsManager;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.group.Group;

@Singleton
public class Scheduler {
	
	private static final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());

	@Schedule(hour="0", minute="10")
	public void getDefaultGroup() {
		
		Client client = Clients.builder()
	    		.setClientCredentials(new TokenClientCredentials(SecretsManager.getOktaApiKey()))
	    		.setOrgUrl(SecretsManager.getOktaOrgUrl())
	    		.build();	
		
		Group group = client.getGroup(SecretsManager.getOktaDefaultGroupId());
		
		LOGGER.info("**** Identity Group: " + group.getResourceHref());
	}
}
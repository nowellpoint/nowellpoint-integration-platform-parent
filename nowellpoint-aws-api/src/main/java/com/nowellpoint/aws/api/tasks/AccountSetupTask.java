package com.nowellpoint.aws.api.tasks;

import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.SearchResult;
import com.nowellpoint.aws.model.admin.Properties;

public class AccountSetupTask implements Callable<String> {
	
	private static final Logger LOGGER = Logger.getLogger(AccountSetupTask.class);
	
	private Account account;
	
	public AccountSetupTask(Account account) {
		this.account = account;
	}

	@Override
	public String call() throws Exception {
		
		String directoryId = System.getProperty(Properties.STORMPATH_DIRECTORY_ID);
		String apiEndpoint = System.getProperty(Properties.STORMPATH_API_ENDPOINT);
		String apiKeyId = System.getProperty(Properties.STORMPATH_API_KEY_ID);
		String apiKeySecret = System.getProperty(Properties.STORMPATH_API_KEY_SECRET);
		
		String href = null;
		
		HttpResponse httpResponse = RestResource.get(apiEndpoint)
				.basicAuthorization(apiKeyId, apiKeySecret)
				.accept(MediaType.APPLICATION_JSON)
				.path("directories")
				.path(directoryId)
				.path("accounts")
				.path("?username=".concat(account.getUsername()))
				.execute();
			
		SearchResult searchResult = httpResponse.getEntity(SearchResult.class);
		
		if (searchResult.getSize() == 0) {
			
			httpResponse = RestResource.post(apiEndpoint)
					.contentType(MediaType.APPLICATION_JSON)
					.path("directories")
					.path(directoryId)
					.path("accounts")
					.basicAuthorization(apiKeyId, apiKeySecret)
					.body(account)
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() != Status.CREATED) {
				throw new Exception(httpResponse.getAsString());
			}
			
			account = httpResponse.getEntity(Account.class);
			
			href = account.getHref();
			
		} else {
			
			href = searchResult.getItems().get(0).getHref();
			
			httpResponse = RestResource.post(href)
					.contentType(MediaType.APPLICATION_JSON)
					.basicAuthorization(apiKeyId, apiKeySecret)
					.body(account)
					.execute();
				
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() != Status.OK) {
				throw new Exception(httpResponse.getAsString());
			}
		}
		
		return href;
	}
}
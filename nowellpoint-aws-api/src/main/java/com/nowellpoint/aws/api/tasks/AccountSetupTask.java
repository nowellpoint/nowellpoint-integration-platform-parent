package com.nowellpoint.aws.api.tasks;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.SearchResult;
import com.nowellpoint.aws.model.admin.Properties;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class AccountSetupTask implements Callable<Account> {
	
	private static final Logger LOGGER = Logger.getLogger(AccountSetupTask.class);
	
	private Account account;
	
	public AccountSetupTask(Account account) {
		this.account = account;
	}

	@Override
	public Account call() throws Exception {
		
		String directoryId = System.getProperty(Properties.STORMPATH_DIRECTORY_ID);
		String apiEndpoint = System.getProperty(Properties.STORMPATH_API_ENDPOINT);
		String apiKeyId = System.getProperty(Properties.STORMPATH_API_KEY_ID);
		String apiKeySecret = System.getProperty(Properties.STORMPATH_API_KEY_SECRET);
		
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
			
			LOGGER.info("Create Account Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() != Status.CREATED) {
				throw new Exception(httpResponse.getAsString());
			}
			
			account = httpResponse.getEntity(Account.class);
			
			System.out.println("Account: " + account.getEmailVerificationToken().getHref());
			
		} else {
			
			String href = searchResult.getItems().get(0).getHref();
			
			httpResponse = RestResource.post(href)
					.contentType(MediaType.APPLICATION_JSON)
					.basicAuthorization(apiKeyId, apiKeySecret)
					.body(account)
					.execute();
				
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() != Status.OK) {
				throw new Exception(httpResponse.getAsString());
			}
			
			account = httpResponse.getEntity(Account.class);
		}
		
		Email from = new Email();
		from.setEmail("administrator@nowellpoint.com");
		from.setName("Nowellpoint Support");
	    
	    Email to = new Email();
	    to.setEmail(account.getUsername());
	    to.setName(account.getFullName());
	    
	    Content content = new Content();
	    content.setType("text/html");
	    content.setValue("<html><body>some text here</body></html>");
	    
	    LOGGER.info("token href: " + account.getEmailVerificationToken().getHref());
	    LOGGER.info(account.getEmailVerificationToken().getHref().substring(account.getEmailVerificationToken().getHref().lastIndexOf("/") + 1));
	    
	    String emailVerificationToken = account.getEmailVerificationToken().getHref().substring(account.getEmailVerificationToken().getHref().lastIndexOf("/") + 1);
	    
	    Personalization personalization = new Personalization();
	    personalization.addTo(to);
	    personalization.addSubstitution("%name%", "John Herson");
	    personalization.addSubstitution("%emailVerificationToken%", String.format("http://localhost/rest/email-verification-tokens/%s", emailVerificationToken));
	    
	    Mail mail = new Mail();
	    mail.setFrom(from);
	    mail.addContent(content);
	    mail.setTemplateId("3e2b0449-2ff8-40cb-86eb-32cad32886de");
	    mail.addPersonalization(personalization);

	    SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
	    
	    Request request = new Request();
	    try {
	    	request.method = Method.POST;
	    	request.endpoint = "mail/send";
	    	request.body = mail.build();
	    	Response response = sendgrid.api(request);
	    	LOGGER.info(response.statusCode);
	    } catch (IOException e) {
	    	LOGGER.error(e);
	    }
		
		return account;
	}
}
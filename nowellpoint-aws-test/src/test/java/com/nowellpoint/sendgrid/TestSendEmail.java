package com.nowellpoint.sendgrid;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.aws.model.admin.Properties;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.account.EmailVerificationToken;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;

public class TestSendEmail {
	
	@BeforeClass
	public static void init() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}
	
	@Test
	public void sendEmail() {
		
		ApiKey apiKey = ApiKeys.builder()
				.setId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.setSecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.build();
		
		Client client = Clients.builder()
				.setApiKey(apiKey)
				.build();
		
		Application application = client.getResource(System.getProperty(Properties.STORMPATH_API_ENDPOINT).concat("/applications/")
				.concat(System.getProperty(Properties.STORMPATH_APPLICATION_ID)), Application.class);

        Account account = client.instantiate(Account.class);

        //Set the account properties
        account.setGivenName("Joe")
                .setSurname("Quickstart_Stormtrooper")
                .setUsername("tk421")  
                .setEmail("tk421@stormpath.com")
                .setPassword("Changeme1")
                .setStatus(AccountStatus.UNVERIFIED);
        

        // Create the account using the existing Application object
        account = application.createAccount(account);
        
        EmailVerificationToken emailVerificationToken = account.getEmailVerificationToken();
        
        account.delete();
		
		Email from = new Email();
		from.setEmail("administrator@nowellpoint.com");
		from.setName("Nowellpoint Support");
	    
	    Email to = new Email();
	    to.setEmail("john.d.herson@gmail.com");
	    to.setName("John Herson");
	    
	    Content content = new Content();
	    content.setType("text/html");
	    content.setValue("<html><body>some text here</body></html>");
	    
	    Personalization personalization = new Personalization();
	    personalization.addTo(to);
	    personalization.addSubstitution("%name%", "John Herson");
	    personalization.addSubstitution("%emailVerificationToken%", emailVerificationToken.getHref());
	    
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
	      System.out.println(response.statusCode);
	      System.out.println(response.body);
	      System.out.println(response.headers);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
}
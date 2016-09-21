package com.nowellpoint.api.service;

import com.sendgrid.SendGrid;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;

import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.model.idp.Account;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;

public class EmailService {
	
	private static final Logger LOGGER = Logger.getLogger(EmailService.class);
	
	private static final SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
	
	public void sendEmailVerificationMessage(Account account, String emailVerificationToken) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail("administrator@nowellpoint.com");
				from.setName("Nowellpoint Support");
			    
			    Email to = new Email();
			    to.setEmail(account.getUsername());
			    to.setName(account.getFullName());
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    
			    URI emailVerificationUrl = UriBuilder.fromUri(System.getProperty(Properties.VERIFY_EMAIL_REDIRECT))
						.queryParam("emailVerificationToken", "{emailVerificationToken}")
						.build(emailVerificationToken);
				
				LOGGER.info(emailVerificationUrl);
			    
			    Personalization personalization = new Personalization();
			    personalization.addTo(to);
			    personalization.addSubstitution("%name%", account.getFullName());
			    personalization.addSubstitution("%emailVerificationToken%", emailVerificationUrl.toString());
			    
			    Mail mail = new Mail();
			    mail.setFrom(from);
			    mail.addContent(content);
			    mail.setTemplateId("3e2b0449-2ff8-40cb-86eb-32cad32886de");
			    mail.addPersonalization(personalization);
			    
			    Request request = new Request();
			    try {
			    	request.method = Method.POST;
			    	request.endpoint = "mail/send";
			    	request.body = mail.build();
			    	Response response = sendgrid.api(request);
			    	LOGGER.info("sendEmailVerificationMessage: " + response.statusCode + " " + response.body);
			    } catch (IOException e) {
			    	LOGGER.error(e);
			    }
			}
		});
	}
	
	public void sendWelcomeMessage(Account account) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail("administrator@nowellpoint.com");
				from.setName("Nowellpoint Support");
			    
			    Email to = new Email();
			    to.setEmail(account.getUsername());
			    to.setName(account.getFullName());
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    	    
			    Personalization personalization = new Personalization();
			    personalization.addTo(to);
			    personalization.addSubstitution("%name%", account.getFullName());
			    personalization.addSubstitution("%username%", account.getUsername());
			    
			    Mail mail = new Mail();
			    mail.setFrom(from);
			    mail.addContent(content);
			    mail.setTemplateId("a676dfe8-5da1-4674-a269-b4a147dd459e");
			    mail.addPersonalization(personalization);
			    
			    Request request = new Request();
			    try {
			    	request.method = Method.POST;
			    	request.endpoint = "mail/send";
			    	request.body = mail.build();
			    	Response response = sendgrid.api(request);
			    	LOGGER.info("sendWelcomeMessage: " + response.statusCode + " " + response.body);
			    } catch (IOException e) {
			    	LOGGER.error(e);
			    }
			}
		});	
	}
}
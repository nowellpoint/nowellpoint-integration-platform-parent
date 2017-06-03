package com.nowellpoint.api.rest.service;

import com.sendgrid.SendGrid;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;

import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.util.Properties;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;

public class SendGridEmailService implements EmailService {
	
	private static final Logger LOGGER = Logger.getLogger(SendGridEmailService.class);
	
	private static final SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
	
	@Override
	public void sendEmailVerificationMessage(String email, String name, String emailVerificationToken) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail("administrator@nowellpoint.com");
				from.setName("Nowellpoint Support");
			    
			    Email to = new Email();
			    to.setEmail(email);
			    to.setName(name);
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    
			    URI emailVerificationUrl = UriBuilder.fromUri(System.getProperty(Properties.VERIFY_EMAIL_REDIRECT))
						.queryParam("emailVerificationToken", "{emailVerificationToken}")
						.build(emailVerificationToken);
				
				LOGGER.info(emailVerificationUrl);
			    
			    Personalization personalization = new Personalization();
			    personalization.addTo(to);
			    personalization.addSubstitution("%name%", name);
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
	
	@Override
	public void sendWelcomeMessage(String email, String username, String name) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail("administrator@nowellpoint.com");
				from.setName("Nowellpoint Support");
			    
			    Email to = new Email();
			    to.setEmail(email);
			    to.setName(name);
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    	    
			    Personalization personalization = new Personalization();
			    personalization.addTo(to);
			    personalization.addSubstitution("%name%", name);
			    personalization.addSubstitution("%username%", username);
			    
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
			    	LOGGER.debug("sendWelcomeMessage: " + response.statusCode + " " + response.body);
			    } catch (IOException e) {
			    	LOGGER.error(e);
			    }
			}
		});	
	}
	
	@Override
	public void sendInvoiceMessage(String email, String name) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail("billing@nowellpoint.com");
				from.setName("Nowellpoint Billing");
			    
			    Email to = new Email();
			    to.setEmail(email);
			    to.setName(name);
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    	    
			    Personalization personalization = new Personalization();
			    personalization.addTo(to);
			    personalization.addSubstitution("%name%", name);
			    
			    Mail mail = new Mail();
			    mail.setFrom(from);
			    mail.addContent(content);
			    mail.setTemplateId("d38cc1d5-d2ec-4b21-83b5-84b23aa44bc8");
			    mail.addPersonalization(personalization);
			    
			    Request request = new Request();
			    try {
			    	request.method = Method.POST;
			    	request.endpoint = "mail/send";
			    	request.body = mail.build();
			    	Response response = sendgrid.api(request);
			    	LOGGER.debug("sendInvoiceMessage: " + response.statusCode + " " + response.body);
			    } catch (IOException e) {
			    	LOGGER.error(e);
			    }
			}
		});	
	}
}
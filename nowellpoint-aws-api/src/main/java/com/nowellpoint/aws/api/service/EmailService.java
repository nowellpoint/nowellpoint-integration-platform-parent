package com.nowellpoint.aws.api.service;

import com.sendgrid.SendGrid;

import java.io.IOException;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.model.admin.Properties;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;

public class EmailService {
	
	private static final Logger LOGGER = Logger.getLogger(EmailService.class);
	
	public void sendWelcomeEmail(String name, String email, String emailVerificationToken) {
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
	    personalization.addSubstitution("%name%", "John Herson");
	    personalization.addSubstitution("%emailVerificationToken%", emailVerificationToken);
	    
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
	}
}
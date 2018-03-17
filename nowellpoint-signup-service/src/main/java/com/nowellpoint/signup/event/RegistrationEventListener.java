package com.nowellpoint.signup.event;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executors;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.nowellpoint.signup.model.Registration;
import com.nowellpoint.util.EnvironmentVariables;
import com.nowellpoint.util.Properties;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class RegistrationEventListener {
	
	@Inject
	private Logger logger;
	
	private static final SendGrid sendgrid = new SendGrid(System.getenv(EnvironmentVariables.SENDGRID_API_KEY));
	
	public void registrationEventHandler(@Observes Registration registration) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail(System.getProperty(Properties.REGISTRATION_FROM_EMAIL));
				from.setName(System.getProperty(Properties.REGISTRATION_FROM_NAME));
			    
			    Email to = new Email();
			    to.setEmail(registration.getEmail());
			    to.setName(registration.getName());
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    
			    Personalization personalization = new Personalization();
			    personalization.addTo(to);
			    personalization.addSubstitution("%name%", registration.getName());
			    personalization.addSubstitution("%emailVerificationToken%", buildEmailVerificationHref(registration.getEmailVerificationToken()).toString());
			    
			    Mail mail = new Mail();
			    mail.setFrom(from);
			    mail.addContent(content);
			    mail.setTemplateId(System.getProperty(Properties.REGISTRATION_EMAIL_TEMPLATE_ID));
			    mail.addPersonalization(personalization);
			    
			    Request request = new Request();
			    try {
			    	request.setMethod(Method.POST);
			    	request.setEndpoint("mail/send");
			    	request.setBody(mail.build());
			    	Response response = sendgrid.api(request);
			    	logger.info("sendEmailVerificationMessage: " + response.getStatusCode() + " " + response.getBody());
			    } catch (IOException e) {
			    	logger.error(e);
			    }
			}
		});
	}
	
	private URI buildEmailVerificationHref(String emailVerificationToken) {
		return UriBuilder.fromUri(System.getProperty(Properties.VERIFY_EMAIL_REDIRECT))
				.queryParam("emailVerificationToken", "{emailVerificationToken}")
				.build(emailVerificationToken);
	}
}
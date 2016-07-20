package com.nowellpoint.aws.api.service;

import com.sendgrid.SendGrid;

import java.io.IOException;

import com.nowellpoint.aws.model.admin.Properties;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;

public class EmailService {
	
	public void sendEmail() {
		SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
		
		Email from = new Email("administrator@nowellpoint.com");
	    String subject = "Hello World from the SendGrid Java Library";
	    Email to = new Email("john.d.herson@gmail.com");
	    Content content = new Content("text/plain", "and easy to do anywhere, even with Java");

	    Mail mail = new Mail(from, subject, to, content);
	    mail.setTemplateId("3e2b0449-2ff8-40cb-86eb-32cad32886de");
	    
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
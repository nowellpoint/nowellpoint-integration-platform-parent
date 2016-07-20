package com.nowellpoint.sendgrid;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.aws.model.admin.Properties;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

public class TestSendEmail {
	
	@BeforeClass
	public static void init() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}
	
	@Test
	public void sendEmail() {
		SendGrid sendgrid = new SendGrid("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");
		
		SendGrid.Email email = new SendGrid.Email();

	    email.addTo("john.d.herson@gmail.com");
	    email.setFrom("administrator@nowellpoint.com");
	    email.setSubject("Sending with SendGrid is Fun");
	    email.setHtml("and easy to do anywhere, even with Java");
	    email.addSubstitution(":name", new String[] {"John Herson"});
	    email.setTemplateId("3e2b0449-2ff8-40cb-86eb-32cad32886de");

	    try {
			SendGrid.Response response = sendgrid.send(email);
			System.out.println(response.getMessage());
			System.out.println(response.getStatus());
		} catch (SendGridException e) {
			e.printStackTrace();
		}
	}

}

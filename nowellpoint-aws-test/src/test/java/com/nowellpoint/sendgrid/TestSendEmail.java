package com.nowellpoint.sendgrid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.jboss.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.util.Properties;
import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class TestSendEmail {
	
	private static final Logger LOGGER = Logger.getLogger(TestSendEmail.class);
	
	@BeforeClass
	public static void init() {
		Properties.loadProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
	}
	
	@Test
	public void testSendEmailWithAttachment() {
		
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
		builder.setBucket("nowellpoint-invoices");
		builder.setKey("myn09y6t");
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(builder.build());
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		S3Object object = s3client.getObject(getObjectRequest);
		InputStream inputStream = object.getObjectContent();
		
		String base64EncodedContent = null;
		try {
			byte[] bytes = IOUtils.toByteArray(inputStream);
			inputStream.close();
			base64EncodedContent = Base64.getEncoder().encodeToString(bytes);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		
		Email from = new Email();
		from.setEmail("billing@nowellpoint.com");
		from.setName("Nowellpoint Billing");
	    
	    Email to = new Email();
	    to.setEmail("john.d.herson@gmail.com");
	    to.setName("John Herson");
	    
	    Content content = new Content();
	    content.setType("text/html");
	    content.setValue("<html><body>some text here</body></html>");
	    	    
	    Personalization personalization = new Personalization();
	    personalization.addTo(to);
	    personalization.addSubstitution("%name%", "John Herson");
	    personalization.addSubstitution("%invoice-link%", "https://localhost:8443/app/account-profile/5808408e392e00330aeef78d/current-plan");
	    
	    Attachments attachments = new Attachments();
	    attachments.setContent(base64EncodedContent);
	    attachments.setType("application/pdf");
	    attachments.setFilename(String.format("invoice_%s.pdf", "myn09y6t"));
	    attachments.setDisposition("attachment");
	    attachments.setContentId("Invoice");
	    
	    Mail mail = new Mail();
	    mail.setFrom(from);
	    mail.addContent(content);
	    mail.setTemplateId("78e36394-86c3-4e16-be73-a3ed3ddae1a8");
	    mail.addPersonalization(personalization);
	    mail.addAttachments(attachments);
	    
	    SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
	    
	    Request request = new Request();
	    try {
	    	request.method = Method.POST;
	    	request.endpoint = "mail/send";
	    	request.body = mail.build();
	    	Response response = sendgrid.api(request);
	    	LOGGER.info("sendInvoiceMessage: " + response.statusCode + " " + response.body);
	    } catch (IOException e) {
	    	LOGGER.error(e);
	    }	
	}
}
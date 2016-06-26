package com.nowellpoint.aws.api.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.aws.api.model.dynamodb.OutboundMessageHandlerConfiguration;
import com.nowellpoint.aws.api.model.sforce.Package;
import com.nowellpoint.aws.api.model.sforce.Type;

public class OutboundMessageService {
	
	public void deploy(final OutboundMessageHandlerConfiguration configuration) throws JAXBException, IOException {
		
		String[][] artifacts = new String[][] {
			{"Outbound_Event__c","CustomObject"},
			{"Outbound_Event__c","Workflow"}
		};
			
		List<Type> types = new ArrayList<Type>();
			
		for (int i = 0; i < artifacts.length; i++) {
			Type type = new Type();
			type.setMembers(artifacts[i][0]);
			type.setName(artifacts[i][1]);
			types.add(type);
		}
			
		Package manifest = new Package();
		manifest.setTypes(types);
		manifest.setVersion(36.0);
			
		StringWriter sw = new StringWriter();
			
		JAXBContext context = JAXBContext.newInstance( Package.class );
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
		marshaller.marshal( manifest, sw);
			
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
		ZipOutputStream zip = new ZipOutputStream(baos);
			
		AmazonS3 s3Client = new AmazonS3Client();
			
		GetObjectRequest getObjectRequest = new GetObjectRequest("aws-microservices", "deployments/Outbound_Event__c.object");
	    	
	    S3Object objects = s3Client.getObject(getObjectRequest);
	    	
	    getObjectRequest = new GetObjectRequest("aws-microservices", "deployments/Outbound_Event__c.workflow");
	    	
	    S3Object workflows = s3Client.getObject(getObjectRequest);
	    	
	    getObjectRequest = new GetObjectRequest("aws-microservices", "deployments/${sobject}_Event_Observer.trigger");
	    	
	    S3Object trigger = s3Client.getObject(getObjectRequest);
	    	
	    getObjectRequest = new GetObjectRequest("aws-microservices", "deployments/${sobject}_Event_Observer.trigger-meta.xml");
	    	
	    byte[] triggerMeta = IOUtils.toByteArray(s3Client.getObject(getObjectRequest).getObjectContent());
	    	
		zip.putNextEntry(new ZipEntry("package.xml"));
		zip.write(sw.toString().getBytes());
		zip.closeEntry();
		zip.putNextEntry(new ZipEntry("objects/Outbound_Event__c.object"));
		zip.write(IOUtils.toByteArray(objects.getObjectContent()));
		zip.closeEntry();
		zip.putNextEntry(new ZipEntry("workflows/Outbound_Event__c.workflow"));
		zip.write(IOUtils.toByteArray(workflows.getObjectContent()));
		zip.closeEntry();
			
		String source = IOUtils.toString(trigger.getObjectContent());
			
		configuration.getQueries().forEach(query -> {
		    try {		    		
				zip.putNextEntry(new ZipEntry(String.format("triggers/%s_Event_Observer.trigger", query.getType())));			
				zip.write(source.replace("${sobject}", query.getType()).getBytes());
				zip.closeEntry();
				zip.putNextEntry(new ZipEntry(String.format("triggers/%s_Event_Observer.trigger-meta.xml", query.getType())));		
				zip.write(triggerMeta);
				zip.closeEntry();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    });
			
		zip.finish();
		zip.close();
			
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			
		ObjectMetadata objectMetadata = new ObjectMetadata();
	    objectMetadata.setContentLength(bais.available());
	    objectMetadata.setContentType("application/zip");
	    	
	    String key = "deployments/".concat(configuration.getServiceInstanceKey())
	    		.concat("/")
	    		.concat(configuration.getEnvironmentName())
	    		.concat("/deploy.zip");
		
		PutObjectRequest putObjectRequest = new PutObjectRequest("aws-microservices", key, bais, objectMetadata);
			
		s3Client.putObject(putObjectRequest);
	}
}
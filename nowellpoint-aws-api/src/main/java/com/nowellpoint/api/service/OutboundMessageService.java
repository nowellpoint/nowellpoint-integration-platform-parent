package com.nowellpoint.api.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.api.model.dynamodb.OutboundMessageHandlerConfiguration;
import com.nowellpoint.api.model.sforce.Package;
import com.nowellpoint.api.model.sforce.Type;
import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.DeployDetails;
import com.sforce.soap.metadata.DeployMessage;
import com.sforce.soap.metadata.DeployOptions;
import com.sforce.soap.metadata.DeployResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class OutboundMessageService {
	
	public String buildPackage(final OutboundMessageHandlerConfiguration configuration) throws JAXBException, IOException {
		
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
		
		configuration.getCallbacks().forEach(query -> {
			Type type = new Type();
			type.setMembers(String.format("%s_Event_Observer", query.getType()));
			type.setName("ApexTrigger");
			types.add(type);
		});
			
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
			
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			
		GetObjectRequest getObjectRequest = new GetObjectRequest("aws-microservices", "deployments/Outbound_Event__c.object");
	    	
	    S3Object objects = s3Client.getObject(getObjectRequest);
	    	
	    getObjectRequest = new GetObjectRequest("aws-microservices", "deployments/Outbound_Event__c.workflow");
	    	
	    String workflows = IOUtils.toString(s3Client.getObject(getObjectRequest).getObjectContent());
    	workflows = workflows.replace("#integrationUser#", configuration.getIntegrationUser());
	    	
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
		zip.write(workflows.getBytes());
		zip.closeEntry();
			
		String source = IOUtils.toString(trigger.getObjectContent());
			
		configuration.getCallbacks().forEach(callback -> {
			List<String> events = new ArrayList<String>();
			if (callback.getCreate()) {
				events.add("after insert");
			}
			if (callback.getUpdate()) {
				events.add("after update");
			}
			if (callback.getDelete()) {
				events.add("after delete");
			}
			
		    try {		    		
				zip.putNextEntry(new ZipEntry(String.format("triggers/%s_Event_Observer.trigger", callback.getType())));			
				zip.write(source.replace("${events}", events.stream().collect(Collectors.joining(", "))).replace("${sobject}", callback.getType()).getBytes());
				zip.closeEntry();
				zip.putNextEntry(new ZipEntry(String.format("triggers/%s_Event_Observer.trigger-meta.xml", callback.getType())));		
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
		
		return key;
	}
	
	public void deployPackage(final PartnerConnection partnerConnection, final String key) throws IOException, ConnectionException, InterruptedException {
		
		MetadataConnection metadataConnection = createMetadataConnection(partnerConnection);
		
		System.out.println(metadataConnection.getSessionHeader().getSessionId());
		
		GetObjectRequest getObjectRequest = new GetObjectRequest("aws-microservices", key);
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
		
		S3Object zipFile = s3Client.getObject(getObjectRequest);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		IOUtils.copy(zipFile.getObjectContent(), baos);
		
		baos.close();
        
        DeployOptions deployOptions = new DeployOptions();
		deployOptions.setSinglePackage(true);
		deployOptions.setPerformRetrieve(false);
		deployOptions.setRollbackOnError(true);
		
		AsyncResult asyncResult = metadataConnection.deploy(baos.toByteArray(), deployOptions);
		
		String asyncResultId = asyncResult.getId();
		
		System.out.println(asyncResultId);
			
		DeployResult deployResult = null;
		
		do {
			Thread.sleep(3000);

			deployResult = metadataConnection.checkDeployStatus(asyncResultId, true);
			
			System.out.println("Status is: " + deployResult.getStatus());
			System.out.println(deployResult.getNumberComponentErrors());
			System.out.println(deployResult.getNumberComponentsDeployed());
			System.out.println(deployResult.getNumberComponentsTotal());
			
			DeployDetails deployDetails = deployResult.getDetails();
			for (DeployMessage message : deployDetails.getComponentFailures()) {
				System.out.println(message.getProblemType() + ": " + message.getProblem() + " " + message.getLineNumber() + " " + message.getFileName());
			}
		} while (!deployResult.isDone());
		
		if (!deployResult.isSuccess() && deployResult.getErrorStatusCode() != null) {
			System.out.println(deployResult.getErrorStatusCode() + " msg: " + deployResult.getErrorMessage());
		}
	}
	
	private static MetadataConnection createMetadataConnection(final PartnerConnection connection) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(connection.getConfig().getServiceEndpoint().replace("/u/", "/m/"));
        config.setSessionId(connection.getConfig().getSessionId());
        return new MetadataConnection(config);
    }
}
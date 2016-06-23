package com.nowellpoint.sforce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.sforce.model.Notification;
import com.nowellpoint.sforce.model.OutboundMessageConfiguration;
import com.nowellpoint.sforce.model.OutboundMessageResult;
import com.nowellpoint.sforce.model.Sobject;

public class OutboundMessageHandlerTask implements Callable<OutboundMessageResult> {
	
	private static HttpClient client = HttpClientBuilder.create().build();
	
	private DynamoDBMapper mapper;
	private Notification notification;
	private String sessionId;
	private String organizationId;
	private String partnerUrl;
	
	public OutboundMessageHandlerTask(DynamoDBMapper mapper, Notification notification, String sessionId, String organizationId, String partnerUrl) {
		this.mapper = mapper;
		this.notification = notification;
		this.sessionId = sessionId;
		this.organizationId = organizationId;
		this.partnerUrl = partnerUrl;
	}

	@Override
	public OutboundMessageResult call() throws Exception {
		
		OutboundMessageResult result = new OutboundMessageResult();
		result.setId(notification.getSobject().getId());
		result.setType(notification.getSobject().getObject());
		
		String url = partnerUrl
				.substring(0, partnerUrl
						.lastIndexOf("/") + 1)
				.replace("/Soap/u/", "/data/v")
				.concat("queryAll");
		
		URIBuilder builder = new URIBuilder(url);
		
		OutboundMessageConfiguration configuration = mapper.load(OutboundMessageConfiguration.class, organizationId, notification.getSobject().getObject());
		
		if (configuration == null) {
			result.setStatus("ERROR");
			result.setErrorMessage(String.format("Unregistered Organization Id: %s or Type: %s", organizationId, notification.getSobject().getObject()));
		}
		
		String query = String.format(configuration.getQueryString(), notification.getSobject().getObjectId());
					
		builder.addParameter("q", query);
		
		HttpGet get = new HttpGet(builder.build());
		get.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + sessionId);
		
		HttpResponse response = client.execute(get);
		
		ObjectNode queryResult = new ObjectMapper().readValue(response.getEntity().getContent(), ObjectNode.class);
		
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			if (queryResult.get("records").isArray()) {
				File file = writeFile(notification.getSobject().getId(), queryResult.get("records"));
				writeToBucket(notification.getSobject(), file);
			}
			result.setStatus("SUCCESS");
		} else {
			result.setStatus("ERROR");
			result.setErrorMessage(result.toString());
		}
		
		return result;
	}
	
	private File writeFile(String id, JsonNode records) throws IOException {
		File file = new File("/tmp/" + id + ".json");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(records.toString());
		bw.close();
		return file;
	}
	
	private void writeToBucket(Sobject sobject, File file) {
		AmazonS3 s3Client = new AmazonS3Client();
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.length());
		metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION); 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		String key = sobject.getObject()
				.concat("/")
				.concat(sobject.getObjectId())
				.concat("/")
				.concat(sdf.format(new Date()));
    	
    	PutObjectRequest putObjectRequest = new PutObjectRequest("salesforce-outbound-messages", key, file).withMetadata(metadata);
    	
    	s3Client.putObject(putObjectRequest);
	}
}
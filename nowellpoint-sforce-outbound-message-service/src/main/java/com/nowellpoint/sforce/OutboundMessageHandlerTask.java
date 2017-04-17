package com.nowellpoint.sforce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.sforce.model.Notification;
import com.nowellpoint.sforce.model.OutboundMessageHandlerConfiguration;
import com.nowellpoint.sforce.model.OutboundMessageResult;
import com.nowellpoint.sforce.model.Callback;
import com.nowellpoint.sforce.model.Sobject;

public class OutboundMessageHandlerTask implements Callable<OutboundMessageResult> {
	
	private static HttpClient client = HttpClientBuilder.create().build();
	
	private OutboundMessageHandlerConfiguration configuration;
	private Notification notification;
	private String sessionId;
	private String partnerUrl;
	
	public OutboundMessageHandlerTask(OutboundMessageHandlerConfiguration configuration, Notification notification, String sessionId, String partnerUrl) {
		this.configuration = configuration;
		this.notification = notification;
		this.sessionId = sessionId;
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
		
		Optional<Callback> callback = configuration.getCallbacks()
				.stream()
				.filter(c -> c.getType().equals(notification.getSobject().getObject()))
				.findFirst();
		
		if (callback.isPresent()) {
			
			String queryString = String.format(callback.get().getQueryString().concat(" Where Id = '%s'"), notification.getSobject().getObjectId());
			
			builder.addParameter("q", queryString);
				
			HttpGet get = new HttpGet(builder.build());
			get.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + sessionId);
				
			HttpResponse response = client.execute(get);
				
			ObjectNode queryResult = new ObjectMapper().readValue(response.getEntity().getContent(), ObjectNode.class);
				
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (queryResult.get("records").isArray()) {
					File file = writeFile(queryResult.get("records"));
					writeToBucket(notification.getSobject(), file, configuration.getBucketName(), configuration.getAwsAccessKey(), configuration.getAwsSecretAccessKey());
				}
				result.setStatus("SUCCESS");
			} else {
				result.setStatus("ERROR");
				result.setErrorMessage(result.toString());
			}
				
		} else {
			result.setStatus("ERROR");
			result.setErrorMessage(String.format("Unregistered Type: %s", notification.getSobject().getObject()));
		}

		return result;
	}
	
	private File writeFile(JsonNode records) throws IOException {
		File file = new File("/tmp/" + UUID.randomUUID().toString());
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(records.toString());
		bw.close();
		return file;
	}
	
	private void writeToBucket(Sobject sobject, File file, String bucketName, String awsAccessKey, String awsSecretAccessKey) {
		AmazonS3 s3Client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretAccessKey)))
				.build();
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.length());
		metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION); 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		
		String key = sobject.getObject()
				.concat("/")
				.concat(sobject.getObjectId())
				.concat("/")
				.concat(sdf.format(new Date()));
    	
    	PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file).withMetadata(metadata);
    	
    	s3Client.putObject(putObjectRequest);
	}
}
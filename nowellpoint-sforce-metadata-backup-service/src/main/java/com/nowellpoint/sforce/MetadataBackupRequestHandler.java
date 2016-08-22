package com.nowellpoint.sforce;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.sforce.model.MetadataBackupRequest;

public class MetadataBackupRequestHandler {
	
	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public String handleEvent(DynamodbEvent event, Context context) {
		
		Long startTime = System.currentTimeMillis();
		
		LambdaLogger logger = context.getLogger();
		
		event.getRecords().stream().filter(record -> "INSERT".equals(record.getEventName())).forEach(record -> {
			
			logger.log("DynamodbEvent received...Event Id: "
					.concat(record.getEventID())
					.concat(" Event Name: " + record.getEventName()));
			
			String organizationId = record.getDynamodb().getKeys().get("OrganizationId").getS();
			String key = record.getDynamodb().getKeys().get("Key").getS();
			
			MetadataBackupRequest metadataBackupRequest = dynamoDBMapper.load(MetadataBackupRequest.class, organizationId, key);
			
			logger.log(metadataBackupRequest.getOrganizationId());
			logger.log(metadataBackupRequest.getSobjectsUrl());
			
			BasicAWSCredentials credentials = new BasicAWSCredentials(metadataBackupRequest.getAwsAccessKey(), metadataBackupRequest.getAwsSecretAccessKey());
			AmazonS3 s3client = new AmazonS3Client(credentials);
			
			Client client = new Client();
			
			try {
				DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
						.setAccessToken(metadataBackupRequest.getSessionId())
						.setSobjectsUrl(metadataBackupRequest.getSobjectsUrl());
				
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
				ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
				
				for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
					logger.log(sobject.getName());
					executor.submit(new Task(
							metadataBackupRequest.getSessionId(),
							metadataBackupRequest.getSobjectsUrl(),
							sobject.getName(),
							organizationId,
							metadataBackupRequest.getBucketName(),
							s3client,
							client));
					
				}
				
				executor.shutdown();
				executor.awaitTermination(30, TimeUnit.SECONDS);
				
				metadataBackupRequest.setStatus("PROCESSED");
			} catch (Exception e) {
				metadataBackupRequest.setStatus("FAILED");
				metadataBackupRequest.setErrorMessage(e.getMessage());
			} finally {
				metadataBackupRequest.setProcessedDate(Date.from(Instant.now()));
				metadataBackupRequest.setProcessDuration(System.currentTimeMillis() - startTime);
				metadataBackupRequest.setSessionId(null);
				dynamoDBMapper.save(metadataBackupRequest);
			}
		});
		
		return "ok";
	}
}

class Task implements Callable<Void> {
	
	private String sessionId;
	private String sobjectsUrl;
	private String sobject;
	private String organizationId;
	private String bucketName;
	private AmazonS3 s3client;
	private Client client;
	
	public Task(String sessionId, String sobjectsUrl, String sobject, String organizationId, String bucketName, AmazonS3 s3client, Client client) {
		this.sessionId = sessionId;
		this.sobjectsUrl = sobjectsUrl;
		this.sobject = sobject;
		this.organizationId = organizationId;
		this.bucketName = bucketName;
		this.s3client = s3client;
		this.client = client;
	}

	@Override
	public Void call() throws Exception {
		DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
				.withAccessToken(sessionId)
				.withSobjectsUrl(sobjectsUrl)
				.withSobject(sobject);
				
		DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);
		
		System.out.println("task: " + sobject);
			
		byte[] input = new ObjectMapper().writeValueAsBytes(describeSobjectResult);
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
			
		String fileName = String.format("%s/%s/%s", organizationId, sobject, sdf.format(Date.from(Instant.now())));
			
		ObjectMetadata objectMetadata = new ObjectMetadata();
	    objectMetadata.setContentLength(input.length);
			
		s3client.putObject(new PutObjectRequest(bucketName, 
				fileName, 
				new ByteArrayInputStream(input),
				objectMetadata));
		
		return null;
	}
	
}
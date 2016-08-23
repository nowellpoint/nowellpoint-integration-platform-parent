package com.nowellpoint.sforce;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
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
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Client client = new Client();
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
	
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
			
			logger.log(organizationId);
			logger.log(metadataBackupRequest.getSobjectsUrl());
			
			try {
				DescribeGlobalSobjectsResult describeGlobalSobjectsResult = describeGlobalSobjectsRequest(metadataBackupRequest);
				System.out.println("complete describeGlobalSobjectsResult");
				describeSobjects(metadataBackupRequest, describeGlobalSobjectsResult);
				System.out.println("complete describeSobjects");
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
	
	private DescribeGlobalSobjectsResult describeGlobalSobjectsRequest(MetadataBackupRequest metadataBackupRequest) throws JsonProcessingException {
		BasicAWSCredentials credentials = new BasicAWSCredentials(metadataBackupRequest.getAwsAccessKey(), metadataBackupRequest.getAwsSecretAccessKey());
		AmazonS3 s3client = new AmazonS3Client(credentials);
		
		String key = String.format("%s/DescribeGlobalResult-%s", metadataBackupRequest.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
		
		System.out.println(key);
		
		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(metadataBackupRequest.getSessionId())
				.setSobjectsUrl(metadataBackupRequest.getSobjectsUrl());
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
		byte[] bytes = objectMapper.writeValueAsBytes(describeGlobalSobjectsResult);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		
		s3client.putObject(new PutObjectRequest(
				metadataBackupRequest.getBucketName(),
				key,
				new ByteArrayInputStream(bytes),
				objectMetadata));
		
		return describeGlobalSobjectsResult;
	}
	
	private void describeSobjects(MetadataBackupRequest metadataBackupRequest, DescribeGlobalSobjectsResult describeGlobalSobjectsResult) throws InterruptedException, ExecutionException, JsonProcessingException {
		BasicAWSCredentials credentials = new BasicAWSCredentials(metadataBackupRequest.getAwsAccessKey(), metadataBackupRequest.getAwsSecretAccessKey());
		AmazonS3 s3client = new AmazonS3Client(credentials);
		
		String key = String.format("%s/DescribeSobjectResult-%s", metadataBackupRequest.getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
		
		System.out.println(key);
				
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		File file = new File("/tmp/DescribeSobjectResult.json");
		
		for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
			executor.submit(new DescribeSobjectTask(
					metadataBackupRequest.getSessionId(),
					metadataBackupRequest.getSobjectsUrl(),
					sobject.getName(),
					client,
					file));
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
		
		if (file.exists()) {
			System.out.println("file exists");
			System.out.println(file.length());
		}
		
		s3client.putObject(new PutObjectRequest(
				metadataBackupRequest.getBucketName(),
				key,
				file));
	}
}

class DescribeSobjectTask implements Callable<Void> {
	
	private String sessionId;
	private String sobjectsUrl;
	private String sobject;
	private Client client;
	private File file;
	
	public DescribeSobjectTask(String sessionId, String sobjectsUrl, String sobject, Client client, File file) {
		this.sessionId = sessionId;
		this.sobjectsUrl = sobjectsUrl;
		this.sobject = sobject;
		this.client = client;
		this.file = file;
	}

	@Override
	public Void call() throws Exception {
		DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
				.withAccessToken(sessionId)
				.withSobjectsUrl(sobjectsUrl)
				.withSobject(sobject);

		DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);
		
		if (file.exists()) {
			System.out.println("file exists");
			System.out.println(file.length());
		}

		//true = append file
		FileWriter fileWritter = new FileWriter(file.getName(),true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(new ObjectMapper().writeValueAsString(describeSobjectResult));
	        bufferWritter.close();

		return null;
	}
}
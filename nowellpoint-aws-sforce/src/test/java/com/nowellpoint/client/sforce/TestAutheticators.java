package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;

public class TestAutheticators {
	
	private static AmazonS3 s3client = new AmazonS3Client();
	
	@BeforeClass
	public static void init() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}
	
	@Test
	public void testUsernamePasswordAuthentication() {
		
		UsernamePasswordGrantRequest request = OauthRequests.USERNAME_PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.USERNAME_PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			assertNotNull(response.getToken());
			assertNotNull(response.getIdentity());
			assertNotNull(response.getIdentity().getAddrCity());
			
			Client client = new Client();
			
			DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setSobjectsUrl(response.getIdentity().getUrls().getSobjects());
			
			DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
			
			ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
			
			for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
				System.out.println(sobject.getName());
				executor.submit(new Task(
						response.getToken().getAccessToken(),
						response.getIdentity().getUrls().getSobjects(),
						sobject.getName(),
						response.getIdentity().getOrganizationId(),
						"aws-microservices",
						s3client,
						client));
				
			}
			
			executor.shutdown();
			executor.awaitTermination(30, TimeUnit.SECONDS);
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
				
		try {

		DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

		byte[] input = new ObjectMapper().writeValueAsBytes(describeSobjectResult);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");

		String fileName = String.format("%s/%s/%s", organizationId, sobject, sdf.format(Date.from(Instant.now())));

		ObjectMetadata objectMetadata = new ObjectMetadata();
	    objectMetadata.setContentLength(input.length);
	    
		s3client.putObject(new PutObjectRequest(bucketName, 
				fileName, 
				new ByteArrayInputStream(input),
				objectMetadata));
		
	    } catch (AmazonClientException e) {
	    	//System.out.println(e.getErrorCode());
	    	System.out.println(e.getMessage());
	    	//System.out.println(e.getErrorMessage());
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    System.out.println("task: " + sobject);
		
		return null;
	}
	
}
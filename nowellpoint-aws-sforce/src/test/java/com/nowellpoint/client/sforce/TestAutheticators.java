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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.UploadPartRequest;
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
			
			List<PartETag> partETags = new ArrayList<PartETag>();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
			
			String keyName = String.format("%s/%s", response.getIdentity().getOrganizationId(), sdf.format(Date.from(Instant.now())));
			
			InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest("aws-microservices", keyName);
			InitiateMultipartUploadResult initResponse = s3client.initiateMultipartUpload(initRequest);
			
			DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setSobjectsUrl(response.getIdentity().getUrls().getSobjects());
			
			DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
			
			ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
			
			int i = 0;
			for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
				System.out.println(sobject.getName());
				executor.submit(new Task(
						response.getToken().getAccessToken(),
						response.getIdentity().getUrls().getSobjects(),
						sobject.getName(),
						"aws-microservices",
						s3client,
						client,
						keyName,
						initResponse.getUploadId(),
						i));
				
				i++;
				
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
	private String bucketName;
	private AmazonS3 s3client;
	private Client client;
	private String key;
	private String uploadId;
	private Integer partNumber;
	
	public Task(String sessionId, String sobjectsUrl, String sobject, String bucketName, AmazonS3 s3client, Client client, String key, String uploadId, Integer partNumber) {
		this.sessionId = sessionId;
		this.sobjectsUrl = sobjectsUrl;
		this.sobject = sobject;
		this.bucketName = bucketName;
		this.s3client = s3client;
		this.client = client;
		this.key = key;
		this.uploadId = uploadId;
		this.partNumber = partNumber;
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
		
		UploadPartRequest uploadRequest = new UploadPartRequest()
	            .withBucketName(bucketName)
	            .withKey(key)
	            .withUploadId(uploadId)
	            .withPartNumber(partNumber)
	            .withInputStream(new ByteArrayInputStream(input))
	            .withPartSize(input.length);
		
		PartETag tag = s3client.uploadPart(uploadRequest).getPartETag();
		System.out.println(tag);

		
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
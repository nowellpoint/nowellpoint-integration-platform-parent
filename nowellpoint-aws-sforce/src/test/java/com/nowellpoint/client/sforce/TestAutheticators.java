package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
			
			List<DescribeSobjectResult> describeResults = new ArrayList<DescribeSobjectResult>();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
			
			String keyName = String.format("%s/%s", response.getIdentity().getOrganizationId(), sdf.format(Date.from(Instant.now())));
			
			System.out.println(keyName);
			
			DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setSobjectsUrl(response.getIdentity().getUrls().getSobjects());
			
			DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
					
			ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
			
			List<Future<DescribeSobjectResult>> tasks = new ArrayList<Future<DescribeSobjectResult>>();
			
			for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
				Future<DescribeSobjectResult> task = executor.submit(new Task(
						response.getToken().getAccessToken(),
						response.getIdentity().getUrls().getSobjects(),
						sobject.getName(),
						client));
				
				tasks.add(task);
			}
			
			executor.shutdown();
			executor.awaitTermination(30, TimeUnit.SECONDS);
			
			for (Future<DescribeSobjectResult> task : tasks) {
				describeResults.add(task.get());
			}
			
			byte[] bytes = new ObjectMapper().writeValueAsBytes(describeResults);
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(bytes.length);
			
			s3client.putObject(new PutObjectRequest(
					"aws-microservices",
					keyName,
					new ByteArrayInputStream(bytes),
					objectMetadata));
			
			System.out.println("getting object");
			GetObjectRequest getObjectRequest = new GetObjectRequest("aws-microservices", keyName);
			S3Object s3object = s3client.getObject(getObjectRequest);
			
			System.out.println(IOUtils.toString(s3object.getObjectContent()));
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (AmazonClientException | IOException e) {
	    	System.out.println(e.getMessage());
	    } catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}

class Task implements Callable<DescribeSobjectResult> {
	
	private String sessionId;
	private String sobjectsUrl;
	private String sobject;
	private Client client;
	
	public Task(String sessionId, String sobjectsUrl, String sobject, Client client) {
		this.sessionId = sessionId;
		this.sobjectsUrl = sobjectsUrl;
		this.sobject = sobject;
		this.client = client;
	}

	@Override
	public DescribeSobjectResult call() throws Exception {
		DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
				.withAccessToken(sessionId)
				.withSobjectsUrl(sobjectsUrl)
				.withSobject(sobject);

		DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

		return describeSobjectResult;
	}
}
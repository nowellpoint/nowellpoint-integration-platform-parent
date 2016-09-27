package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;

public class TestAutheticators {
	
	private static AmazonS3 s3client = new AmazonS3Client();
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static Client client = new Client();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
	private static String bucketName = "nowellpoint-metadata-backups";
	
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
			
			long startTime = System.currentTimeMillis();
			
			processDescribeGlobal(response);
			processDescribeSobject(response);
			
			System.out.println("Process duration (ms): " + (System.currentTimeMillis() - startTime));
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (ClientException e) {
			System.out.println(e.getErrorDescription());
			System.out.println(e.getError());
			System.out.println(e.getStatusCode());
		} catch (AmazonClientException | IOException e) {
	    	System.out.println(e.getMessage());
	    } catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private void processDescribeGlobal(OauthAuthenticationResponse response) throws JsonProcessingException {
		String keyName = String.format("%s/DescribeGlobalResult-%s", response.getIdentity().getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
		
		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(response.getToken().getAccessToken())
				.setSobjectsUrl(response.getIdentity().getUrls().getSobjects());
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
		byte[] bytes = objectMapper.writeValueAsBytes(describeGlobalSobjectsResult);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		
		s3client.putObject(new PutObjectRequest(
				bucketName,
				keyName,
				new ByteArrayInputStream(bytes),
				objectMetadata));
	}
	
	private void processDescribeSobject(OauthAuthenticationResponse response) throws InterruptedException, ExecutionException, JsonProcessingException {
		List<DescribeSobjectResult> describeResults = new ArrayList<DescribeSobjectResult>();
		
		String keyName = String.format("%s/DescribeSobjectResult-%s", response.getIdentity().getOrganizationId(), dateFormat.format(Date.from(Instant.now())));
		
		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(response.getToken().getAccessToken())
				.setSobjectsUrl(response.getIdentity().getUrls().getSobjects());
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
				
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		List<Future<DescribeSobjectResult>> tasks = new ArrayList<Future<DescribeSobjectResult>>();
		
		for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
			Future<DescribeSobjectResult> task = executor.submit(() -> {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(response.getToken().getAccessToken())
						.withSobjectsUrl(response.getIdentity().getUrls().getSobjects())
						.withSobject(sobject.getName());

				DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

				return describeSobjectResult;
			});
			
			tasks.add(task);
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
		
		for (Future<DescribeSobjectResult> task : tasks) {
			describeResults.add(task.get());
		}
		
		byte[] bytes = objectMapper.writeValueAsBytes(describeResults);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		
		s3client.putObject(new PutObjectRequest(
				bucketName,
				keyName,
				new ByteArrayInputStream(bytes),
				objectMetadata));
	}
}
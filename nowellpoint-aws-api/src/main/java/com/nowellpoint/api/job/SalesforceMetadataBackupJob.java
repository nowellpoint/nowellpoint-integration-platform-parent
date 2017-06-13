package com.nowellpoint.api.job;

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

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.api.model.document.Job;
import com.nowellpoint.api.model.document.JobExecution;
import com.nowellpoint.api.model.document.JobOutput;
import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.api.rest.domain.SalesforceConnectionString;
import com.nowellpoint.api.rest.service.SalesforceServiceImpl;
import com.nowellpoint.api.rest.service.VaultEntryServiceImpl;
import com.nowellpoint.api.service.SalesforceService;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.GetIdentityRequest;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sforce.ws.ConnectionException;

public class SalesforceMetadataBackupJob extends AbstractCacheService implements org.quartz.Job {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceMetadataBackupJob.class);
	
	private DocumentManagerFactory documentManagerFactory;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final String bucketName = "nowellpoint-metadata-backups";
	private static final Client client = new Client();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		documentManagerFactory = Datastore.getCurrentSession();
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		
		Job job = documentManager.fetch(Job.class, new ObjectId(context.getJobDetail()
				.getKey()
				.getName()));
		
		job.setStatus("RUNNING");
		
		documentManager.replaceOne(job);
		
		set(job.getId().toString(), job);
		
		VaultEntryService vaultEntryService = new VaultEntryServiceImpl();
		
		VaultEntry vaultEntry = vaultEntryService.retrive(job.getSource().getConnectionString());
		
		SalesforceConnectionString salesforceConnectionString = SalesforceConnectionString.of(vaultEntry.getValue());
		
		SalesforceService salesforceService = new SalesforceServiceImpl();
		
		try {
			
			// 
			// Authenticate
			//
			
			Token token = salesforceService.login(salesforceConnectionString);
			
			//
			// Get Identity
			//
			
			Identity identity = getIdentity(token.getAccessToken(), token.getId());
			
			// 
	    	// DescribeGlobal
	    	//
			
			DescribeGlobalSobjectsResult describeGlobalSobjectsResult = describeGlobalSobjectsRequest(token.getAccessToken(), identity.getUrls().getSobjects());

			//
			// Save result to S3 and add link to the job
			//
	    	
	    	job.addJobOutput(saveJobOutput(
	    			DescribeGlobalSobjectsResult.class.getSimpleName(), 
	    			context.getFireInstanceId(), 
	    			identity.getOrganizationId(), 
	    			describeGlobalSobjectsResult));
	    	
	    	//
	    	// DescribeSobjectResult - build full description first run, capture changes for each subsequent run
	    	//

	    	List<DescribeSobjectResult> describeSobjectResults = describeSobjects(token.getAccessToken(), identity.getUrls().getSobjects(), describeGlobalSobjectsResult, job.getFireTime());
	    	
	    	//
	    	// if describeSobjectResults is not empty then save to S3
	    	//
	    	
	    	if (! describeSobjectResults.isEmpty()) {
	    		
	    		//
				// Save result to S3 and add link to the job
				//
		    	
		    	job.addJobOutput(saveJobOutput(
		    			DescribeSobjectResult.class.getSimpleName(), 
		    			context.getFireInstanceId(), 
		    			identity.getOrganizationId(), 
		    			describeSobjectResults));
		    	
		    	//
		    	// get theme
		    	//

		    	Theme theme = getTheme(token.getAccessToken(), identity.getUrls().getRest());
		    	
		    	//
				// Save result to S3 and add link to the job
				//
		    	
		    	job.addJobOutput(saveJobOutput(
		    			Theme.class.getSimpleName(), 
		    			context.getFireInstanceId(), 
		    			identity.getOrganizationId(), 
		    			theme));
	    	}
	    	
	    	job.setStatus("SUCCESS");
			
		} catch (OauthException e) {
			job.setStatus("FAILED");
			job.setFailureMessage(e.getError());
		} catch (ConnectionException | JsonProcessingException | InterruptedException | ExecutionException e) {
			job.setStatus("FAILED");
			job.setFailureMessage(e.getMessage());
		}
		
		JobExecution jobExecution = new JobExecution();
		jobExecution.setFireInstanceId(context.getFireInstanceId());
		jobExecution.setFireTime(context.getFireTime());
		jobExecution.setJobRunTime(System.currentTimeMillis() - context.getFireTime().getTime());
		jobExecution.setStatus(job.getStatus());
		jobExecution.setFailureMessage(job.getFailureMessage());
		
		job.addJobExecution(jobExecution);
		job.setNumberOfExecutions(job.getNumberOfExecutions().intValue() + 1);
		job.setJobRunTime(System.currentTimeMillis() - context.getFireTime().getTime());
		job.setFireTime(context.getFireTime());
		job.setNextFireTime(context.getNextFireTime());
		
		if (Assert.isNotNull(context.getNextFireTime())) {			
			job.setStatus("SCHEDULED");
			job.getSchedule().setRunAt(context.getNextFireTime());
		} else {
			job.setStatus("COMPLETED");
		}
		
		documentManager.replaceOne(job);
		
		set(job.getId().toString(), job);
		
		//
		// compile and send notification
		//
		
		String format = "%s%1s%n";
		
		String message = new StringBuilder()
				.append(String.format(format, "Scheduled Job: ", job.getJobName()))
				.append(String.format(format, "Completion Date: ", Date.from(Instant.now()).toString()))
				.append(String.format(format, "Status: ", jobExecution.getStatus()))
				.append(Assert.isNotNull(jobExecution.getFailureMessage()) ? String.format(format, "Exception: ", jobExecution.getFailureMessage()) : "")
				.toString();
		
		if (Assert.isNotNull(job.getNotificationEmail())) {
    		
    		String subject = String.format("[%s] Scheduled Job Request Complete", salesforceConnectionString.getOrganizationId());
    		
    		try {
				sendNotification(job.getNotificationEmail(), subject, message);
			} catch (IOException e) {
				LOGGER.error("Unable to send email: " + e.getMessage());
			}
		}
		
		if (Assert.isNotNull(job.getSlackWebhookUrl())) {
			
			ObjectNode payload = new ObjectMapper().createObjectNode()
					.put("text", message)
					.put("username", "Nowellpoint Notification Service");
			
			HttpResponse httpResponse = RestResource.post(job.getSlackWebhookUrl())
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.body(payload)
					.execute();
			
			if (httpResponse.getStatusCode() != Status.OK) {
				LOGGER.error(httpResponse.getAsString());
			}			
		}
	}
	
	/**
	 * 
	 * 
	 * @param accessToken
	 * @param identityId
	 * @return
	 * 
	 * 
	 */
	
	private Identity getIdentity(String accessToken, String identityId) {
		GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
    			.setAccessToken(accessToken)
    			.setId(identityId);
    	
    	Identity identity = client.getIdentity(getIdentityRequest);
    	
    	return identity;
	}
	
	/**
	 * 
	 * 
	 * @param email
	 * @param name
	 * @param body
	 * @throws IOException
	 * 
	 */
	
	private void sendNotification(String email, String subject, String body) throws IOException {
		Email from = new Email();
		from.setEmail("support@nowellpoint.com");
		from.setName("Nowellpoint Support");
	    
	    Email to = new Email();
	    to.setEmail(email);
	    
	    Content content = new Content();
	    content.setType("text/plain");
	    content.setValue(body);
	    
	    SendGrid sendgrid = new SendGrid(System.getProperty(Properties.SENDGRID_API_KEY));
	    
	    Personalization personalization = new Personalization();
	    personalization.addTo(to);
	    personalization.setSubject(subject);
	    
	    Mail mail = new Mail();
	    mail.setFrom(from);
	    mail.addContent(content);
	    mail.addPersonalization(personalization);
	    
	    Request request = new Request();
	    request.method = Method.POST;
    	request.endpoint = "mail/send";
    	request.body = mail.build();
    	sendgrid.api(request);
	}
	
	/**
	 * 
	 * @param path
	 * @param object
	 * @return
	 * @throws JsonProcessingException
	 */
	
	private JobOutput saveJobOutput(String fileName, String fireInstanceId, String path, Object object) throws JsonProcessingException {
		
		String keyName = generateKeyName(fileName, path);
   
    	PutObjectResult result = putObject(keyName, object);
    	
    	return JobOutput.of(fireInstanceId, fileName, result.getMetadata().getContentLength(), bucketName, keyName);
	}

	/**
	 * 
	 * 
	 * @param keyName
	 * @param object
	 * @throws JsonProcessingException
	 * 
	 * 
	 */
	
	private PutObjectResult putObject(String keyName, Object object) throws JsonProcessingException {
		byte[] bytes = objectMapper.writeValueAsBytes(object);
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		PutObjectResult result = s3client.putObject(new PutObjectRequest(
				bucketName,
				keyName,
				new ByteArrayInputStream(bytes),
				objectMetadata));
		
		result.getMetadata().setContentLength(bytes.length);
		
		return result;
	}
	
	/**
	 * 
	 * 
	 * @param accessToken
	 * @param sobjectsUrl
	 * @return
	 * @throws JsonProcessingException
	 * 
	 * 
	 */
	
	private DescribeGlobalSobjectsResult describeGlobalSobjectsRequest(String accessToken, String sobjectsUrl) throws JsonProcessingException {

		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(accessToken)
				.setSobjectsUrl(sobjectsUrl);
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
		return describeGlobalSobjectsResult;
	}
	
	/**
	 * 
	 * 
	 * @param accessToken
	 * @param sobjectsUrl
	 * @param describeGlobalSobjectsResult
	 * @param modifiedSince
	 * @return list of DescribeSobjectResult
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws JsonProcessingException
	 * 
	 * 
	 */
	
	private List<DescribeSobjectResult> describeSobjects(String accessToken, String sobjectsUrl, DescribeGlobalSobjectsResult describeGlobalSobjectsResult, Date modifiedSince) throws InterruptedException, ExecutionException, JsonProcessingException {
		List<DescribeSobjectResult> describeResults = new ArrayList<DescribeSobjectResult>();
		List<Future<DescribeSobjectResult>> tasks = new ArrayList<Future<DescribeSobjectResult>>();
				
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
			Future<DescribeSobjectResult> task = executor.submit(() -> {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(accessToken)
						.withSobjectsUrl(sobjectsUrl)
						.withSobject(sobject.getName())
						.withIfModifiedSince(modifiedSince);

				DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

				return describeSobjectResult;
			});
			
			tasks.add(task);
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
		
		for (Future<DescribeSobjectResult> task : tasks) {
			if (Assert.isNotNull(task.get())) {
				describeResults.add(task.get());
			}
		}
		
		return describeResults;
	}
	
	/**
	 * 
	 */
	
	private Theme getTheme(String accessToken, String restEndpoint) {
		ThemeRequest themeRequest = new ThemeRequest()
				.withAccessToken(accessToken)
				.withRestEndpoint(restEndpoint);
		
		Theme theme = client.getTheme(themeRequest);
		
		return theme;
	}
	
	private String generateKeyName(String fileType, String organizationId) {
		return String.format("%s/%s-%s", organizationId, fileType, dateFormat.format(Date.from(Instant.now())).replace(":", ""));
	}
}
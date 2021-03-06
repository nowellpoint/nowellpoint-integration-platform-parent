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

import org.jboss.logging.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

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
import com.nowellpoint.api.rest.domain.JobScheduleOptions;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.api.service.impl.VaultEntryServiceImpl;
import com.nowellpoint.api.util.EnvUtil;
import com.nowellpoint.api.util.EnvUtil.Variable;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.SObject;
import com.nowellpoint.client.sforce.test.Client;
import com.nowellpoint.client.sforce.test.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.test.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.test.GetIdentityRequest;
import com.nowellpoint.client.sforce.test.ThemeRequest;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Assert;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;

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
		
		JobDetail jobDetail = context.getJobDetail();
		
		LOGGER.info(jobDetail.getKey().getName());
		
		LOGGER.info(jobDetail.getJobDataMap().containsKey(jobDetail.getKey().getName()));
		
		LOGGER.info(jobDetail.getJobDataMap().get(jobDetail.getKey().getName()).getClass().getName());
		
		Job job = (Job) jobDetail.getJobDataMap().get(jobDetail.getKey().getName());
		
		job.setStatus("RUNNING");
		
		documentManager.replaceOne(job);
		
		set(job.getId().toString(), job);
		
		VaultEntryService vaultEntryService = new VaultEntryServiceImpl();
		
		VaultEntry vaultEntry = vaultEntryService.retrive(job.getSource().getConnectionString());
		
		try {
			
			// 
			// Authenticate
			//
			
			Token token = null;
			
			//
			// Get Identity
			//
			
			Identity identity = getIdentity(token.getAccessToken(), token.getId());
			
			// 
	    	// DescribeGlobal
	    	//
			
			DescribeGlobalResult describeGlobalSobjectsResult = describeGlobalSobjectsRequest(token.getAccessToken(), identity.getUrls().getSobjects());

			//
			// Save result to S3 and add link to the job
			//
	    	
	    	job.addJobOutput(saveJobOutput(
	    			DescribeGlobalResult.class.getSimpleName(), 
	    			context.getFireInstanceId(), 
	    			identity.getOrganizationId(), 
	    			describeGlobalSobjectsResult));
	    	
	    	//
	    	// DescribeSobjectResult - build full description first run, capture changes for each subsequent run
	    	//

	    	List<DescribeResult> describeSobjectResults = describeSobjects(token.getAccessToken(), identity.getUrls().getSobjects(), describeGlobalSobjectsResult, job.getFireTime());
	    	
	    	//
	    	// if describeSobjectResults is not empty then save to S3
	    	//
	    	
	    	if (! describeSobjectResults.isEmpty()) {
	    		
	    		//
				// Save result to S3 and add link to the job
				//
		    	
		    	job.addJobOutput(saveJobOutput(
		    			DescribeResult.class.getSimpleName(), 
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
	    	
	    	job.setStatus("COMPLETED");
			
		} catch (OauthException e) {
			job.setStatus("FAILED");
			job.setFailureMessage(e.getError());
		} catch (JsonProcessingException | InterruptedException | ExecutionException e) {
			job.setStatus("FAILED");
			job.setFailureMessage(e.getMessage());
			try {
				context.getScheduler().deleteJob(context.getJobDetail().getKey());
			} catch (SchedulerException se) {
				LOGGER.error(se);
			}
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
		
		if (JobScheduleOptions.RUN_ON_SCHEDULE.equals(job.getScheduleOption()) || JobScheduleOptions.RUN_ON_SPECIFIC_DAYS.equals(job.getScheduleOption())) {
			job.setStatus("SCHEDULED");
			job.setNextFireTime(context.getNextFireTime());
			job.getSchedule().setRunAt(context.getNextFireTime());
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
    		
    		String subject = String.format("[%s] Scheduled Job Request Complete", "");
    		
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
	    
	    SendGrid sendgrid = new SendGrid(EnvUtil.getValue(Variable.SENDGRID_API_KEY));
	    
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
	
	private DescribeGlobalResult describeGlobalSobjectsRequest(String accessToken, String sobjectsUrl) throws JsonProcessingException {

		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(accessToken)
				.setSobjectsUrl(sobjectsUrl);
		
		DescribeGlobalResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
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
	
	private List<DescribeResult> describeSobjects(String accessToken, String sobjectsUrl, DescribeGlobalResult describeGlobalSobjectsResult, Date modifiedSince) throws InterruptedException, ExecutionException, JsonProcessingException {
		List<DescribeResult> describeResults = new ArrayList<DescribeResult>();
		List<Future<DescribeResult>> tasks = new ArrayList<Future<DescribeResult>>();
				
		ExecutorService executor = Executors.newFixedThreadPool(describeGlobalSobjectsResult.getSobjects().size());
		
		for (SObject sobject : describeGlobalSobjectsResult.getSobjects()) {
			Future<DescribeResult> task = executor.submit(() -> {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(accessToken)
						.withSobjectsUrl(sobjectsUrl)
						.withSobject(sobject.getName())
						.withIfModifiedSince(modifiedSince);

				DescribeResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

				return describeSobjectResult;
			});
			
			tasks.add(task);
		}
		
		executor.shutdown();
		executor.awaitTermination(30, TimeUnit.SECONDS);
		
		for (Future<DescribeResult> task : tasks) {
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
package com.nowellpoint.aws.lambda.s3;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.sforce.OutboundMessage;
import com.nowellpoint.aws.sforce.SalesforceResource;
import com.nowellpoint.aws.util.Configuration;
import com.nowellpoint.aws.util.MongoQuery;

public class OutboundMessageHandler {
	
	private static AmazonS3Encryption encryptionClient;
	
	private static AmazonSNS snsClient;
	
	private static MongoClientURI mongoClientURI;
	
	private static MongoClient mongoClient;
	
	private static MongoDatabase mongoDatabase;
	
	private static ObjectId organizationId;
	
	private static ObjectId userId;
	
	private static SalesforceResource salesforceService = new SalesforceResource();
	
	public OutboundMessageHandler() throws IOException {
	   
        encryptionClient = new AmazonS3EncryptionClient(
        		new EnvironmentVariableCredentialsProvider(),
        		new KMSEncryptionMaterialsProvider(Configuration.getAwsKmsKeyId()), 
        		new CryptoConfiguration());
        
        snsClient = new AmazonSNSClient();
	}
	
	public String handleEvent(S3Event s3event, Context context) {
		context.getLogger().log("s3event received");
		s3event.getRecords().forEach(record -> {
			OutboundMessage outboundMessage;
			try {
				String bucketName = record.getS3().getBucket().getName();
				String objectKey = record.getS3().getObject().getKey();
				outboundMessage = getObject(bucketName, objectKey);
				processOutboundMessage(outboundMessage, context);
				deleteObject(bucketName, objectKey);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
		
		String message = new StringBuilder()
				.append("Processed: ")
				.append(s3event.getRecords().size())
				.append(" outbound message(s)")
				.toString();
		
		publishMessage(message);
		
		return context.getAwsRequestId();
	}
	
	private OutboundMessage getObject(String bucketName, String objectKey) throws IOException {
		S3Object downloadedObject = encryptionClient.getObject(bucketName, objectKey);
        byte[] decrypted = IOUtils.toByteArray(downloadedObject.getObjectContent());
		return new ObjectMapper().readValue(new String(decrypted, Charset.forName(StandardCharsets.UTF_8.displayName())), OutboundMessage.class);
	}
	
	private void deleteObject(String bucketName, String objectKey) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, objectKey);
		encryptionClient.deleteObject(deleteObjectRequest);
	}
	
	private void publishMessage(String message) {
		PublishRequest publishRequest = new PublishRequest().withTopicArn("arn:aws:sns:us-east-1:600862814314:OUTBOUND_MESSAGE").withSubject(message).withMessage("Outbound Message Processed");
		snsClient.publish(publishRequest);
	}
	
	private void processOutboundMessage(OutboundMessage outboundMessage, Context context) throws IOException {
		
		try {
			
			/**
			 * 
			 */

			mongoClientURI = new MongoClientURI("mongodb://".concat(Configuration.getMongoClientUri()));
			
			/**
			 * 
			 */
			
			mongoClient = new MongoClient(mongoClientURI);
					
			/**
			 * 
			 */ 

			mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
			
			/**
			 * 
			 */

			organizationId = getOrganizationBySalesforceId(outboundMessage.getOrganizationId());

			/**
			 * 
			 */

			userId = getCurrentUser(outboundMessage.getPartnerUrl(), outboundMessage.getSessionId());
			
			/**
			 * 
			 */

			ExecutorService service = Executors.newFixedThreadPool(outboundMessage.getNotifications().size());
			
			/**
			 * 
			 */

			Set<OutboundMessageEvent> outboundMessageEvents = new HashSet<OutboundMessageEvent>();

			/**
			 * 
			 */

			if (organizationId != null) {
				
				/**
				 * create event requests from notifications
				 */
				
				outboundMessage.getNotifications().stream().forEach(notification -> {
					
					OutboundMessageEventRequest outboundMessageEventRequest = new OutboundMessageEventRequest().withLogger(context.getLogger())
							.withPartnerURL(outboundMessage.getPartnerUrl())
							.withMongoDatabase(mongoDatabase)
							.withNotification(notification)
							.withOrganizationId(organizationId)
							.withSessionId(outboundMessage.getSessionId())
							.withUserId(userId);
					
					outboundMessageEvents.add(new OutboundMessageEvent(outboundMessageEventRequest));
					
				});

				/**
				 * process outbound message events
				 */

				List<Future<OutboundMessageEventResponse>> outboundMessageEventResponses = service.invokeAll(outboundMessageEvents);
				service.shutdown();
				service.awaitTermination(30, TimeUnit.SECONDS);

				/**
				 * 
				 */

				List<Document> events = new ArrayList<Document>();
				for (Future<OutboundMessageEventResponse> outboundMessageEventResponse : outboundMessageEventResponses) {
					Document event = new Document().append("organizationId", organizationId)
							.append("userId", userId)
							.append("objectId", outboundMessageEventResponse.get().getObjectId())
							.append("collection", outboundMessageEventResponse.get().getCollection())
							.append("action", outboundMessageEventResponse.get().getAction() != null ? outboundMessageEventResponse.get().getAction().toString() : null)
							.append("status", outboundMessageEventResponse.get().getStatus() != null ? outboundMessageEventResponse.get().getStatus().toString() : null)
							.append("executionTime", outboundMessageEventResponse.get().getExecutionTime())
							.append("exceptionMessage", outboundMessageEventResponse.get().getExceptionMessage());
						
					events.add(event);
				}

				Document transaction = new Document().append("awsRequestId", context.getAwsRequestId())
						.append("executionTime", (1000 * 60 - context.getRemainingTimeInMillis()))
						.append("functionName", context.getFunctionName())
						.append("logGroupName", context.getLogGroupName())
						.append("logStreamName", context.getLogStreamName())
						.append("transactionDate", new Date())
						.append("events", events);
					
				mongoDatabase.getCollection("aws.transactions").insertOne(transaction);	
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new IOException(e);
		} finally {
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
	}
	
	private ObjectId getOrganizationBySalesforceId(String salesforceId) {
		return getDocumentBySalesforceId("organizations", salesforceId);
	}
	
	private ObjectId getUserBySalesforceId(String salesforceId) {
		return getDocumentBySalesforceId("users", salesforceId);
	}
	
	private ObjectId getDocumentBySalesforceId(String collection, String salesforceId) {
		Document document = new MongoQuery().withMongoDatabase(mongoDatabase)
				.withCollectionName(collection)
				.withSalesforceId(salesforceId)
				.find();
		
		if (document == null) {
			return null;
		}
		
		return document.getObjectId("_id");
	}
	
	private ObjectId getCurrentUser(String partnerUrl, String sessionId) throws IOException {
		JsonNode user = salesforceService.getCurrentUser(partnerUrl, sessionId);
		String userId = user.get("Id").asText();
		return getUserBySalesforceId(userId);		
	}
}
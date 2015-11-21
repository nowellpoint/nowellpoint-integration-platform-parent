package com.nowellpoint.aws.lambda.dynamodb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.lambda.s3.OutboundMessageEvent;
import com.nowellpoint.aws.lambda.s3.OutboundMessageEventRequest;
import com.nowellpoint.aws.lambda.s3.OutboundMessageEventResponse;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.TransactionStatus;
import com.nowellpoint.aws.model.sforce.OutboundMessage;
import com.nowellpoint.aws.sforce.SalesforceResource;
import com.nowellpoint.aws.util.MongoQuery;

public class OutboundMessageHandler {
	
	private static DynamoDB dynamoDB;
	private static AWSKMS kms;
	private static SalesforceResource salesforceResource;
	private static MongoClientURI mongoClientURI;	
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	private static ObjectId organizationId;
	private static ObjectId userId;
	
	public OutboundMessageHandler() {
		dynamoDB = new DynamoDB(new AmazonDynamoDBClient());
		kms = new AWSKMSClient();
		salesforceResource = new SalesforceResource();
	}

	public String handleEvent(DynamodbEvent event, Context context) {
		context.getLogger().log(new Date() + " DynamodbEvent received...");
		event.getRecords().forEach(record -> {
			record.getDynamodb().getKeys().forEach( (key, value) -> {
				
				context.getLogger().log(new Date() + " Processing Transaction Id: " + value.getS());
				
				PrimaryKey primaryKey = new PrimaryKey("Id", value.getS());
				
				Table table = dynamoDB.getTable("Transactions");
				
				Item item = table.getItem(primaryKey);
				String payload = item.getString("Payload");
				
				ByteBuffer ciphertext = ByteBuffer.wrap(Base64.decode(payload.getBytes()));
				DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(ciphertext);
				ByteBuffer plainText = kms.decrypt(decryptRequest).getPlaintext();
				
				String decrypted = new String(plainText.array(), Charset.forName("UTF-8"));
				
				try {
					OutboundMessage outboundMessage = new ObjectMapper().readValue(decrypted, OutboundMessage.class);
					processOutboundMessage(outboundMessage, context);
					item.withString("Status", TransactionStatus.COMPLETE.name());
				} catch (Exception e) {
					item.withString("Status", TransactionStatus.ERROR.name());
					item.withString("ErrorMessage", e.getMessage());
					e.printStackTrace();
				} finally {
					item.withString("OrganizationId", organizationId.toString());
					item.withString("UserId", userId.toString());
					table.putItem(item);
				}
			});
		});
		
		return context.getAwsRequestId();
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
		Optional<Document> query = Optional.ofNullable(new MongoQuery().withMongoDatabase(mongoDatabase)
				.withCollectionName(collection)
				.withSalesforceId(salesforceId)
				.find());
		
		ObjectId id = null;
		
		if (query.isPresent()) {
			id = query.get().getObjectId("_id");
		}
		
		return id;
	}
	
	private ObjectId getCurrentUser(String partnerUrl, String sessionId) throws IOException {
		JsonNode user = salesforceResource.getCurrentUser(partnerUrl, sessionId);
		String userId = user.get("Id").asText();
		return getUserBySalesforceId(userId);		
	}
}
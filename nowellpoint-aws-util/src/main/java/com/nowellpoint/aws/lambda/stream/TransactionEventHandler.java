package com.nowellpoint.aws.lambda.stream;

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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.Transaction;
import com.nowellpoint.aws.model.TransactionResult;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.PropertyStore;
import com.nowellpoint.aws.model.sforce.OutboundMessage;
import com.nowellpoint.aws.sforce.SalesforceResource;
import com.nowellpoint.aws.tools.MongoQuery;

public class TransactionEventHandler {
	
	private static DynamoDBMapper mapper;
	private static AWSKMS kms;
	private static SalesforceResource salesforceResource;
	private static MongoClientURI mongoClientURI;
	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;
	
	public TransactionEventHandler() {
		mapper = new DynamoDBMapper(new AmazonDynamoDBClient());
		kms = new AWSKMSClient();
		salesforceResource = new SalesforceResource();
		mongoClient = new MongoClient(mongoClientURI);
		mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
	}

	public String handleEvent(DynamodbEvent event, Context context) {
		
		/**
		 * 
		 */
		
		long startTime = System.currentTimeMillis();
		
		/**
		 * 
		 */
		
		LambdaLogger logger = context.getLogger();
		
		/**
		 * 
		 */
		
		mongoClientURI = new MongoClientURI("mongodb://".concat(Properties.getProperty(PropertyStore.MONGODB, Properties.MONGO_CLIENT_URI)));
		
		/**
		 * 
		 */
		
		event.getRecords().stream().filter(record -> "INSERT".equals(record.getEventName())).forEach(record -> {
			
			/**
			 * 
			 */
			
			logger.log(new Date() + " DynamodbEvent received...Event Id: "
					.concat(record.getEventID())
					.concat(" Event Name: " + record.getEventName()));
	        
			/**
			 * 
			 */
			
	        record.getDynamodb().getKeys().forEach( (key, value) -> {	        	
	        	
	        	/**
	        	 * 
	        	 */
	        	
	        	String id = value.getS();
	        	
	        	/**
	        	 * 
	        	 */
	        	
	        	logger.log(new Date() + " Processing Transaction Id: " + id);
	        	
	        	/**
	        	 * 
	        	 */
	        	
	        	Transaction transaction = mapper.load(Transaction.class, id);
	        		
	        	/**
	        	 * 
	        	 */
	        	
	        	if (transaction != null) {
	        		ByteBuffer ciphertext = ByteBuffer.wrap(Base64.decode(transaction.getPayload().getBytes()));
	        		DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(ciphertext);
	        		ByteBuffer plainText = kms.decrypt(decryptRequest).getPlaintext();
	        			
	        		String decrypted = new String(plainText.array(), Charset.forName("UTF-8"));
	        			
	        		try {
	        			OutboundMessage outboundMessage = new ObjectMapper().readValue(decrypted, OutboundMessage.class);
	        			TransactionResult result = processOutboundMessage(outboundMessage, context);
	        			transaction.setOrganizationId(result.getOrganizationId());
	        			transaction.setUserId(result.getUserId());
	        			transaction.setExecutionTime(Long.valueOf(System.currentTimeMillis() - startTime));
	        			transaction.setRecordCount(event.getRecords().size());
	        			transaction.setStatus(Transaction.TransactionStatus.COMPLETE.name());
	        		} catch (IOException e) {
	        			transaction.setStatus(Transaction.TransactionStatus.ERROR.name());
	        			transaction.setErrorMessage(e.getMessage());
	        		} finally {
	        			mapper.save(transaction);
	        		}
	        	}
	        });
		});
		
		return "Successfully processed " + event.getRecords().size() + " records.";
	}
	
	private TransactionResult processOutboundMessage(OutboundMessage outboundMessage, Context context) throws MongoException, IOException {
		
		/**
		 * 
		 */
		
		TransactionResult result = new TransactionResult();
		
		/**
		 * 
		 */
		
		try {
			
			/**
			 * 
			 */

			ObjectId organizationId = getOrganizationBySalesforceId(outboundMessage.getOrganizationId());

			/**
			 * 
			 */

			ObjectId userId = getCurrentUser(outboundMessage.getPartnerUrl(), outboundMessage.getSessionId());
			
			/**
			 * 
			 */

			ExecutorService service = Executors.newFixedThreadPool(outboundMessage.getNotifications().size());
			
			/**
			 * 
			 */

			Set<TransactionEvent> outboundMessageEvents = new HashSet<TransactionEvent>();

			/**
			 * 
			 */

			if (organizationId != null) {
				
				/**
				 * create event requests from notifications
				 */
				
				outboundMessage.getNotifications().stream().forEach(notification -> {
					
					TransactionEventRequest outboundMessageEventRequest = new TransactionEventRequest().withLogger(context.getLogger())
							.withPartnerURL(outboundMessage.getPartnerUrl())
							.withMongoDatabase(mongoDatabase)
							.withNotification(notification)
							.withOrganizationId(organizationId)
							.withSessionId(outboundMessage.getSessionId())
							.withUserId(userId);
					
					outboundMessageEvents.add(new TransactionEvent(outboundMessageEventRequest));
					
				});

				/**
				 * process outbound message events
				 */

				List<Future<TransactionEventResponse>> outboundMessageEventResponses = service.invokeAll(outboundMessageEvents);
				service.shutdown();
				service.awaitTermination(30, TimeUnit.SECONDS);
				
				/**
				 * 
				 */

				List<Document> events = new ArrayList<Document>();
				for (Future<TransactionEventResponse> outboundMessageEventResponse : outboundMessageEventResponses) {
					
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
					
				mongoDatabase.getCollection("inbound.events").insertMany(events);
				
				result.setOrganizationId(organizationId.toString());
				result.setUserId(userId.toString());				
			}
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new IOException(e);
		} finally {
			if (mongoClient != null) {
				mongoClient.close();
			}
		}
		
		/**
		 * 
		 */
		
		return result;
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
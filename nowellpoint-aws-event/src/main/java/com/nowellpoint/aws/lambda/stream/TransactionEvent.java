package com.nowellpoint.aws.lambda.stream;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.nowellpoint.aws.http.MediaType;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.FieldMappingEntry;
import com.nowellpoint.aws.model.Mapping;
import com.nowellpoint.aws.model.sforce.Notification;
import com.nowellpoint.aws.sforce.SalesforceUrlFactory;

public class TransactionEvent implements Callable<TransactionEventResponse> {
	
	private static Map<String,Mapping> mappingCache = new HashMap<String,Mapping>();
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	private LambdaLogger logger;
	
	private String partnerURL;
	
	private String sessionId;
	
	private MongoDatabase mongoDatabase;
	
	private ObjectId userId; 
	
	private ObjectId organizationId;
	
	private Notification notification;

	public TransactionEvent(TransactionEventRequest outboundMessageProcessorRequest) {
		this.logger = outboundMessageProcessorRequest.getLogger();
		this.partnerURL = outboundMessageProcessorRequest.getPartnerURL();
		this.sessionId = outboundMessageProcessorRequest.getSessionId();
		this.mongoDatabase = outboundMessageProcessorRequest.getMongoDatabase();
		this.userId = outboundMessageProcessorRequest.getUserId(); 
		this.organizationId = outboundMessageProcessorRequest.getOrganizationId();
		this.notification = outboundMessageProcessorRequest.getNotification();
	}

	@Override
	public TransactionEventResponse call() {
		
		/**
		 * 
		 */
		
		long startTime = System.currentTimeMillis();
		
		/**
		 * 
		 */
		
		try {

			/**
			 * 
			 */

			Mapping mapping = getMapping(notification.getSobject().getType());

			/**
			 * 
			 */

			String collectionName = mapping.getCollectionName();
			String sourceType = mapping.getSourceType();

			/**
			 * 
			 */
			
			String query = buildQueryString(sourceType, mapping.getFieldMappingEntries(), notification.getSobject().getId());

			/**
			 * 
			 */
			
			logger.log(query);

			/**
			 * 
			 */
			
			HttpResponse response = RestResource.get(SalesforceUrlFactory.queryURL(partnerURL))
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(sessionId)
					.accept(MediaType.APPLICATION_JSON)
					.queryParameter("q", query)
					.execute();
			
			logger.log("Query response status: " + response.getStatusCode() + " Target: " + response.getURL());
			
			if (response.getStatusCode() != 200) {
				throw new IOException("Query failed: " + response.getEntity());
			} 
			
			/**
			 * 
			 */
			
			JsonNode jsonNode = response.getEntity(JsonNode.class);

			/**
			 * 
			 */

			Document document = convertToDocument(mapping.getFieldMappingEntries(), jsonNode, collectionName, notification.getSobject().getId());
			
			/**
			 * 
			 */

			MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
			if (document.getObjectId("_id") != null) {
				collection.replaceOne( new Document("_id", document.getObjectId("_id") ), document );
			} else {
				collection.insertOne(document);
			}
			
			/**
			 * 
			 */

			TransactionEventResponse.Action action = document.getInteger("version") > 1 ? 
					TransactionEventResponse.Action.UPDATE : 
						TransactionEventResponse.Action.CREATE;
			
			/**
			 * 
			 */

			TransactionEventResponse outboundMessageEventResponse = new TransactionEventResponse().withObjectId(document.getObjectId("_id"))
					.withStatus(TransactionEventResponse.Status.SUCCESS)
					.withAction(action)
					.withCollection(collectionName)
					.withExecutionTime((System.currentTimeMillis() - startTime));
			
			/**
			 * 
			 */
			
			logger.log("execution time: " + outboundMessageEventResponse.getExecutionTime());
			
			/**
			 * 
			 */
			
			return outboundMessageEventResponse;
		
		} catch (Exception e) {
			
			/**
			 * 
			 */
			
			logger.log("ERROR: " + e.getMessage());
			
			/**
			 * 
			 */
			
			TransactionEventResponse outboundMessageEventResponse = new TransactionEventResponse()
					.withStatus(TransactionEventResponse.Status.FAIL)
					.withExceptionMessage(e.getMessage())
					.withExecutionTime(System.currentTimeMillis() - startTime);
			
			/**
			 * 
			 */
						
			return outboundMessageEventResponse;
		}
	}
	
	private Mapping getMapping(String sourceType) throws JsonParseException, JsonMappingException, IOException {
		Mapping mapping = mappingCache.get(sourceType);
		
		if (mapping == null) {
			Optional<Document> query = Optional.ofNullable( mongoDatabase.getCollection("mappings").find( Filters.eq ( "sourceType", sourceType ) ).first() );

			Document document = query.orElseThrow(() -> new IllegalArgumentException("Unable to find mapping for ".concat(sourceType)));
			
			mapping = objectMapper.readValue(document.toJson(), Mapping.class);

			mappingCache.put(sourceType, mapping);
		}
		
		return mapping;
	}
	
	private String buildQueryString(String sObject, List<FieldMappingEntry> fieldMappingEntries, String id) throws JsonParseException, JsonMappingException, IOException {
		StringBuilder sb = new StringBuilder().append( "SELECT " )
				.append( parseFieldMappingEntries( fieldMappingEntries ) )
				.append( " FROM " )
				.append( sObject )
				.append( " WHERE Id = '" )
				.append( id )
				.append( "'" );
		
		logger.log(sb.toString());
		
		return URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8.displayName());
	}
	
	private String parseFieldMappingEntries(List<FieldMappingEntry> fieldMappingEntries) {
		return fieldMappingEntries.stream()
				.filter( isMapped() )
				.map( entry -> entry.getSource() )
				.collect( Collectors.joining (",") );
	}
	
	private static Predicate<FieldMappingEntry> isMapped() {
		return entry -> entry.getMapped();
	}
	
	private Document convertToDocument(List<FieldMappingEntry> fieldMappingEntries, JsonNode source, String collectionName, String salesforceId) throws JsonProcessingException, IOException {

		JsonNode records = source.get("records");

		Map<String, Object> record = objectMapper.readValue(records.get(0).traverse(), new TypeReference<Map<String, Object>>() {});
		
		Date timestamp = new Date();
		
		Optional<Document> query = Optional.ofNullable(mongoDatabase.getCollection("mappings").find( Filters.eq ( "salesforceId", salesforceId ) ).first() );

		Document destination = query.orElse(new Document().append("createdDate", timestamp)
				.append("createdById", userId)
				.append("organizationId", organizationId)
				.append("readOnly", Boolean.FALSE)
				.append("version", new Integer(0)));

		destination.append("version", destination.getInteger("version") + 1)
				.append("lastModifiedDate", timestamp)
				.append("lastModifiedById", userId);
	
		Map<String, FieldMappingEntry> fieldMap = fieldMappingEntries.stream().collect(Collectors.toMap(FieldMappingEntry::getSource, entry -> entry));

		for (String key : record.keySet()) {
			if ("attributes".equals(key)) {
				destination.append("attributes", record.get("attributes"));
			} else {
				Object value = record.get(key);
				FieldMappingEntry entry = fieldMap.get(key);
				String name = entry.getDestination().getName();
				destination.append(name, value);
			}
		}

		return destination;
	}
}
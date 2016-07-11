package com.nowellpoint.aws.api.model;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.DBRef;

public class DBRefDeserializer extends JsonDeserializer<DBRef> {

	@Override
	public DBRef deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		
		ObjectCodec oc = parser.getCodec();
		ObjectNode node = (ObjectNode) oc.readTree(parser);
		
		String collectionName = null;
		ObjectId objectId = null;
		
		if (node.get("$ref") != null) {
        	JsonNode ref = node.get("$ref");
        	collectionName = ref.asText();
        }
		
		if (node.get("$id") != null) {
			JsonNode oid = node.get("$id").get("$oid");
			objectId = new ObjectId(oid.asText());
		}
		
		return new DBRef(collectionName, objectId);
	}	
}
package com.nowellpoint.mongodb.document;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

	@Override
	public ObjectId deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		TreeNode treeNode = parser.readValueAsTree();
        JsonNode oid = ((JsonNode) treeNode).get("$oid");
		if( oid != null ) {
            return new ObjectId(oid.asText());
        } else {
            return new ObjectId(((JsonNode)treeNode).asText());
        }
	}	
}
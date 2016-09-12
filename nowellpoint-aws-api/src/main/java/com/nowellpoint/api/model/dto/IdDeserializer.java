package com.nowellpoint.api.model.dto;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class IdDeserializer extends JsonDeserializer<Id> {

	@Override
	public Id deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		TreeNode treeNode = parser.readValueAsTree();
        JsonNode id = ((JsonNode) treeNode).get("id");
        return new Id(id.asText());
	}	
}
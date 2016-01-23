package com.nowellpoint.aws.model.data;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DateDeserializer extends JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		
		ObjectCodec oc = parser.getCodec();
		ObjectNode node = (ObjectNode) oc.readTree(parser);
        
        if (node.get("$date") != null) {
        	return new Date(node.get("$date").asLong());
        }
        
        return null;
	}	
}
package com.nowellpoint.aws.model.data;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

	@Override
	public ObjectId deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		JsonToken jsonToken = parser.nextToken();
			
		if (JsonToken.FIELD_NAME.equals(jsonToken)) {
			String fieldName = parser.getCurrentName();		        
		    if ("$oid".equals(fieldName)) {
		        parser.nextToken();
		        return new ObjectId(parser.getValueAsString());
		    }
		}
		
		return null;
	}	
}
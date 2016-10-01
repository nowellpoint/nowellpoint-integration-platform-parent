package com.nowellpoint.mongodb.document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DateDeserializer extends JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		
		ObjectCodec oc = parser.getCodec();
		ObjectNode node = (ObjectNode) oc.readTree(parser);
        
		Date value = null;
		
        if (node.get("$date") != null) {
        	JsonNode field = node.get("$date");
        	if (field.isLong()) {
        		value = new Date(field.asLong());
        	} else {
        		try {
    				value = sdf.parse(field.asText());
    			} catch (ParseException e) {
    				throw new IOException(e);
    			}
        	}
        }
        return value;
	}	
}
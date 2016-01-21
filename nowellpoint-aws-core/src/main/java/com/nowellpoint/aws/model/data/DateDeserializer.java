package com.nowellpoint.aws.model.data;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateDeserializer extends JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		JsonToken jsonToken = parser.nextToken();
			
		if (JsonToken.FIELD_NAME.equals(jsonToken)) {
			String fieldName = parser.getCurrentName();		        
		    if ("$date".equals(fieldName)) {
		        parser.nextToken();
		        try {       	
					return sdf.parse(parser.getValueAsString());
				} catch (ParseException e) {
					throw new IOException(e);
				}
		    }
		}
		
		return null;
	}	
}
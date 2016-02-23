package com.nowellpoint.aws.data.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		if (value != null) {
			generator.writeStartObject();
			generator.writeFieldName("$date");
			generator.writeString(sdf.format(value));
			generator.writeEndObject();
		} else {
			generator.writeNull();
		}
	}
}
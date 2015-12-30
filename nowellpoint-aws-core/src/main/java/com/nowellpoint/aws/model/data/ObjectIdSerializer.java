package com.nowellpoint.aws.model.data;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

	@Override
	public void serialize(ObjectId value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (value != null) {
			generator.writeString(value.toString());
		} else {
			generator.writeNull();
		}
	}
}
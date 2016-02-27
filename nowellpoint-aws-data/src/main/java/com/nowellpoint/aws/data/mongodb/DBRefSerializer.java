package com.nowellpoint.aws.data.mongodb;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mongodb.DBRef;

public class DBRefSerializer extends JsonSerializer<DBRef> {

	@Override
	public void serialize(DBRef value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		if(value == null) {
			generator.writeNull();
		} else {
			generator.writeStartObject();
			generator.writeFieldName("$ref");
			generator.writeString(value.getCollectionName());
			generator.writeFieldName("$id");
			generator.writeStartObject();
			generator.writeFieldName("$oid");
			generator.writeString(value.getId().toString());
			generator.writeEndObject();
			generator.writeEndObject();
        }
	}
}
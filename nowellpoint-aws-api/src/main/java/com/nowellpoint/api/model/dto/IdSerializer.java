package com.nowellpoint.api.model.dto;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class IdSerializer extends JsonSerializer<Id> {

	@Override
	public void serialize(Id id, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (id == null) {
			generator.writeNull();
		} else {
        	generator.writeString(id.toString());
        }
	}
}
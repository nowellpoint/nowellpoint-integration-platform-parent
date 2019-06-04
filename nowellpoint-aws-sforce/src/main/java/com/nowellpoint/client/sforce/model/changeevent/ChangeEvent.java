package com.nowellpoint.client.sforce.model.changeevent;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ChangeEvent implements Serializable {
	
	private static final long serialVersionUID = 3537459047661703726L;
	protected static final ObjectMapper mapper = new ObjectMapper();

	@Getter @JsonProperty(value="schema") private String schema;
	@Getter @JsonProperty(value="payload") private Payload payload;
	@Getter @JsonProperty(value="event") private Event event;
	
	public static ChangeEvent of(Map<String,Object> data) throws IOException {
		JsonNode node = mapper.valueToTree(data);
		return mapper.readValue(node.toString(), ChangeEvent.class);
	}
}
package com.nowellpoint.client.sforce.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamingEvent implements Serializable {

	private static final long serialVersionUID = -8426229851500146364L;
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Getter @JsonProperty("event") private Event event;
	@Getter @JsonProperty("sobject") private SObject sobject;
	
	public StreamingEvent() {
		
	}
	
	public static StreamingEvent of(Map<String,Object> data) throws IOException {
		JsonNode node = mapper.valueToTree(data);
		return mapper.readValue(node.toString(), com.nowellpoint.client.sforce.model.StreamingEvent.class);
	}
}
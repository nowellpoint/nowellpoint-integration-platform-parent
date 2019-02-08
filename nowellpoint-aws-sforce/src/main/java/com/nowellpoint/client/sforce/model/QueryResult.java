package com.nowellpoint.client.sforce.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResult {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	@Getter @JsonProperty("totalSize") private Integer totalSize;
	@Getter @JsonProperty("done") private Boolean done;
	@Getter @JsonProperty("nextRecordsUrl") private String nextRecordsUrl;
	@Getter @JsonProperty("records") private JsonNode[] records;
	
	public QueryResult() { }
	
	public <T> Set<T> getRecords(Class<T> valueType) {
		return Arrays.asList(records).stream()
				.map(r -> {
					try {
						return objectMapper.readValue(r.toString(), valueType);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
		})
		.collect(Collectors.toSet());
	}
}
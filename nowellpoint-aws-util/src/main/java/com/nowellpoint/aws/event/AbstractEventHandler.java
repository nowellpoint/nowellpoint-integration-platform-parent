package com.nowellpoint.aws.event;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface AbstractEventHandler {
	
	static ObjectMapper objectMapper = new ObjectMapper();

	public abstract String process(String payload) throws IOException;
}
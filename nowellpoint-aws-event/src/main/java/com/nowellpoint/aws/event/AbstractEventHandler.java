package com.nowellpoint.aws.event;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.Event;

public interface AbstractEventHandler {
	
	static ObjectMapper objectMapper = new ObjectMapper();

	public abstract void process(Event event, Map<String, String> properties, Context context) throws Exception;
	
}
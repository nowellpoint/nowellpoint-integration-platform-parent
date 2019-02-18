package com.nowellpoint.console.util;

import javax.ws.rs.BadRequestException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String render(Object model) {
        try {
			return mapper.writeValueAsString(model);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new BadRequestException(e);
		}
    }
}
package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class JobResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "jobs";
	private static final String START = "start";
	
	public JobResource(Token token) {
		super(token);
	}

	
}
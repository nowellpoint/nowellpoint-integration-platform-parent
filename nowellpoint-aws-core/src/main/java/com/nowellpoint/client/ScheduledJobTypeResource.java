package com.nowellpoint.client;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.ScheduledJobType;

public class ScheduledJobTypeResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "scheduled-job-types";

	public ScheduledJobTypeResource(Token token) {
		super(token);
	}

	public List<ScheduledJobType> getScheduledJobTypesByLanguage(String language) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.queryParameter("languageSidKey", language)
				.execute();
		
		List<ScheduledJobType> scheduledJobTypes = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			scheduledJobTypes = httpResponse.getEntityList(ScheduledJobType.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return scheduledJobTypes;
	}
}
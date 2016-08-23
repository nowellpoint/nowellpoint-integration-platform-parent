package com.nowellpoint.aws.api.tasks;

import java.util.concurrent.Callable;

import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;

public class DescribeSobjectTask implements Callable<DescribeSobjectResult> {
	
	private String sessionId;
	private String sobjectsUrl;
	private String sobject;
	private Client client;
	
	public DescribeSobjectTask(String sessionId, String sobjectsUrl, String sobject, Client client) {
		this.sessionId = sessionId;
		this.sobjectsUrl = sobjectsUrl;
		this.sobject = sobject;
		this.client = client;
	}

	@Override
	public DescribeSobjectResult call() throws Exception {
		DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
				.withAccessToken(sessionId)
				.withSobjectsUrl(sobjectsUrl)
				.withSobject(sobject);

		DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);

		return describeSobjectResult;
	}
}
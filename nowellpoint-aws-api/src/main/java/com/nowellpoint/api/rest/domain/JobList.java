package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class JobList extends DocumentCollectionResource<Job, com.nowellpoint.api.model.document.Job> {
	
	public JobList(Set<com.nowellpoint.api.model.document.Job> documents) {
		super(documents);
	}

	@Override
	protected Class<Job> getItemType() {
		return Job.class;
	}
}
package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class JobList extends DocumentCollectionResource<JobOrig, com.nowellpoint.api.model.document.Job> {
	
	public JobList(Set<com.nowellpoint.api.model.document.Job> documents) {
		super(documents);
	}

	@Override
	protected Class<JobOrig> getItemType() {
		return JobOrig.class;
	}
}
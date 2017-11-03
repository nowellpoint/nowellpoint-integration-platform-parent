package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class JobTypeList extends DocumentCollectionResource<JobType, com.nowellpoint.api.model.document.JobType> {
	
	public JobTypeList(Set<com.nowellpoint.api.model.document.JobType> documents) {
		super(documents);
	}

	@Override
	protected Class<JobType> getItemType() {
		return JobType.class;
	}
}
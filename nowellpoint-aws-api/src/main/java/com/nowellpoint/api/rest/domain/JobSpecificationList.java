package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class JobSpecificationList extends AbstractCollectionResource<JobSpecification, com.nowellpoint.api.model.document.JobSpecification> {
	
	public JobSpecificationList(Set<com.nowellpoint.api.model.document.JobSpecification> documents) {
		super(documents);
	}

	@Override
	protected Class<JobSpecification> getItemType() {
		return JobSpecification.class;
	}
}
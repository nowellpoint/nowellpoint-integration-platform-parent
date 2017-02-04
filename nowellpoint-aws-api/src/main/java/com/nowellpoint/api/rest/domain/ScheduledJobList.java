package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class ScheduledJobList extends AbstractCollectionResource<ScheduledJob, com.nowellpoint.api.model.document.ScheduledJob> {
	
	public ScheduledJobList(Set<com.nowellpoint.api.model.document.ScheduledJob> documents) {
		super(documents);
	}

	@Override
	protected Class<ScheduledJob> getItemType() {
		return ScheduledJob.class;
	}
}
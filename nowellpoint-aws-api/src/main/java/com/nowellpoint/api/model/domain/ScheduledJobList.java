package com.nowellpoint.api.model.domain;

import com.mongodb.client.FindIterable;

public class ScheduledJobList extends AbstractCollectionResource<ScheduledJob, com.nowellpoint.api.model.document.ScheduledJob> {
	
	public ScheduledJobList(FindIterable<com.nowellpoint.api.model.document.ScheduledJob> documents) {
		super(documents);
	}

	@Override
	protected Class<ScheduledJob> getItemType() {
		return ScheduledJob.class;
	}
}
package com.nowellpoint.api.model.domain;

import com.mongodb.client.FindIterable;

public class ScheduledJobTypeList extends AbstractCollectionResource<ScheduledJobType, com.nowellpoint.api.model.document.ScheduledJobType> {
	
	public ScheduledJobTypeList(FindIterable<com.nowellpoint.api.model.document.ScheduledJobType> documents) {
		super(documents);
	}

	@Override
	protected Class<ScheduledJobType> getItemType() {
		return ScheduledJobType.class;
	}
}
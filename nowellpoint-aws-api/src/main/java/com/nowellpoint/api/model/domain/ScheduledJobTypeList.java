package com.nowellpoint.api.model.domain;

import java.util.Set;

public class ScheduledJobTypeList extends AbstractCollectionResource<ScheduledJobType, com.nowellpoint.api.model.document.ScheduledJobType> {
	
	public ScheduledJobTypeList(Set<com.nowellpoint.api.model.document.ScheduledJobType> documents) {
		super(documents);
	}

	@Override
	protected Class<ScheduledJobType> getItemType() {
		return ScheduledJobType.class;
	}
}
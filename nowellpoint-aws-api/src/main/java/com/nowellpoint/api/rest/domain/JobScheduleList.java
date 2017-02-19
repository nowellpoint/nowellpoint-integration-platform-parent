package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class JobScheduleList extends AbstractCollectionResource<JobSchedule, com.nowellpoint.api.model.document.JobSchedule> {
	
	public JobScheduleList(Set<com.nowellpoint.api.model.document.JobSchedule> documents) {
		super(documents);
	}

	@Override
	protected Class<JobSchedule> getItemType() {
		return JobSchedule.class;
	}
}
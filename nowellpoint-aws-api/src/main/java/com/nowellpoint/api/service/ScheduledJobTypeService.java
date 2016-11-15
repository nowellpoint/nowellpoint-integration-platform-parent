package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.model.mapper.ScheduledJobTypeModelMapper;

public class ScheduledJobTypeService extends ScheduledJobTypeModelMapper {

	public ScheduledJobTypeService() {
		
	}
	
	public ScheduledJobType findScheduedJobTypeById(String id) {
		return super.findScheduedJobTypeById(id);
	}
	
	public Set<ScheduledJobType> findByLanguage(String languageSidKey) {
		return super.findByLanguage(languageSidKey);
	}
	
	public void createScheduledJobType(ScheduledJobType scheduledJobType) {
		super.createScheduledJobType(scheduledJobType);
	}
	
	public void updateScheduledJobType(String id, ScheduledJobType scheduledJobType) {
		scheduledJobType.setId(id);
		super.updateScheduledJobType(scheduledJobType);
	}
	
	public void deleteScheduledJobType(String id) {
		ScheduledJobType scheduledJobType = findScheduedJobTypeById(id);
		super.deleteScheduledJob(scheduledJobType);
	}
}
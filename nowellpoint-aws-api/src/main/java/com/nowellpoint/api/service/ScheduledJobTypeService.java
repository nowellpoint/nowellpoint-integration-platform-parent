package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.ScheduledJobType;
import com.nowellpoint.api.model.mapper.ScheduledJobTypeModelMapper;

public class ScheduledJobTypeService extends ScheduledJobTypeModelMapper {

	public ScheduledJobTypeService() {
		
	}
	
	public ScheduledJobType findById(Id id) {
		return super.findScheduedJobTypeById(id);
	}
	
	public Set<ScheduledJobType> findByLanguage(String languageSidKey) {
		return super.findByLanguage(languageSidKey);
	}
	
	public void createScheduledJobType(ScheduledJobType scheduledJobType) {
		super.createScheduledJobType(scheduledJobType);
	}
	
	public void updateScheduledJobType(Id id, ScheduledJobType scheduledJobType) {
		scheduledJobType.setId(id);
		super.updateScheduledJobType(scheduledJobType);
	}
	
	public void deleteScheduledJobType(Id id) {
		ScheduledJobType scheduledJobType = findById(id);
		super.deleteScheduledJob(scheduledJobType);
	}
}
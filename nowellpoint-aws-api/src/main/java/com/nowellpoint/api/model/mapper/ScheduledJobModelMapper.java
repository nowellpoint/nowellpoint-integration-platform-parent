package com.nowellpoint.api.model.mapper;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.ScheduledJob;

/**
 * 
 * 
 * @author jherson
 * 
 *
 */

public class ScheduledJobModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.ScheduledJob> {

	/**
	 * 
	 * 
	 *
	 * 
	 */
	
	public ScheduledJobModelMapper() {
		super(com.nowellpoint.api.model.document.ScheduledJob.class);
	}

	protected ScheduledJob findScheduedJobById(Id id) {
		com.nowellpoint.api.model.document.ScheduledJob document = findById(id.toString());
		return modelMapper.map(document, ScheduledJob.class);
	}
	
	protected void createScheduledJob(ScheduledJob scheduledJob) {
		com.nowellpoint.api.model.document.ScheduledJob document = modelMapper.map(scheduledJob, com.nowellpoint.api.model.document.ScheduledJob.class);
		create(getSubject(), document);
		modelMapper.map(document, scheduledJob);
	}
	
	protected void updateScheduledJob(ScheduledJob scheduledJob) {
		com.nowellpoint.api.model.document.ScheduledJob document = modelMapper.map(scheduledJob, com.nowellpoint.api.model.document.ScheduledJob.class);
		replace(getSubject(), document);
		modelMapper.map(document, scheduledJob);
	}
	
	protected Set<ScheduledJob> findAllByOwner() {
		Set<com.nowellpoint.api.model.document.ScheduledJob> documents = findAllByOwner(getSubject());
		Set<ScheduledJob> scheduledJobs = modelMapper.map(documents, new TypeToken<HashSet<ScheduledJob>>() {}.getType());
		return scheduledJobs;
	}
	
	protected void deleteScheduledJob(ScheduledJob scheduledJob) {
		com.nowellpoint.api.model.document.ScheduledJob document = modelMapper.map(scheduledJob, com.nowellpoint.api.model.document.ScheduledJob.class);
		delete(getSubject(), document);
	}
}
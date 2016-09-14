package com.nowellpoint.api.model.mapper;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.ScheduledJobType;

/**
 * 
 * 
 * @author jherson
 * 
 *
 */

public class ScheduledJobTypeModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.ScheduledJobType> {

	/**
	 * 
	 * 
	 *
	 * 
	 */
	
	public ScheduledJobTypeModelMapper() {
		super(com.nowellpoint.api.model.document.ScheduledJobType.class);
	}
	
	protected Set<ScheduledJobType> findByLanguage(String languageSidKey) {
		Set<com.nowellpoint.api.model.document.ScheduledJobType> documents = find( Filters.eq ( "languageSidKey", languageSidKey ) );
		Set<ScheduledJobType> scheduledJobTypes = modelMapper.map(documents, new TypeToken<HashSet<ScheduledJobType>>() {}.getType());
		return scheduledJobTypes;
	}

	protected ScheduledJobType findScheduedJobTypeById(Id id) {
		com.nowellpoint.api.model.document.ScheduledJobType document = findById(id.toString());
		return modelMapper.map(document, ScheduledJobType.class);
	}
	
	protected void createScheduledJobType(ScheduledJobType scheduledJobType) {
		com.nowellpoint.api.model.document.ScheduledJobType document = modelMapper.map(scheduledJobType, com.nowellpoint.api.model.document.ScheduledJobType.class);
		create(getSubject(), document);
		modelMapper.map(document, scheduledJobType);
	}
	
	protected void updateScheduledJobType(ScheduledJobType scheduledJobType) {
		com.nowellpoint.api.model.document.ScheduledJobType document = modelMapper.map(scheduledJobType, com.nowellpoint.api.model.document.ScheduledJobType.class);
		replace(getSubject(), document);
		modelMapper.map(document, scheduledJobType);
	}
	
	protected void deleteScheduledJob(ScheduledJobType scheduledJobType) {
		com.nowellpoint.api.model.document.ScheduledJobType document = modelMapper.map(scheduledJobType, com.nowellpoint.api.model.document.ScheduledJobType.class);
		delete(getSubject(), document);
	}
}
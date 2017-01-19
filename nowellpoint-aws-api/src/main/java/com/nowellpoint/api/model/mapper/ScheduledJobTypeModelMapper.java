package com.nowellpoint.api.model.mapper;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.model.domain.ScheduledJobTypeList;

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
	
	protected ScheduledJobTypeList findByLanguage(String languageSidKey) {
		FindIterable<com.nowellpoint.api.model.document.ScheduledJobType> documents = query( Filters.eq ( "languageSidKey", languageSidKey ) );
		ScheduledJobTypeList scheduledJobTypes = new ScheduledJobTypeList(documents);
		return scheduledJobTypes;
	}

	protected ScheduledJobType findScheduedJobTypeById(String id) {
		com.nowellpoint.api.model.document.ScheduledJobType document = fetch(id);
		return modelMapper.map(document, ScheduledJobType.class);
	}
	
	protected void createScheduledJobType(ScheduledJobType scheduledJobType) {
		com.nowellpoint.api.model.document.ScheduledJobType document = modelMapper.map(scheduledJobType, com.nowellpoint.api.model.document.ScheduledJobType.class);
		create(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, scheduledJobType);
	}
	
	protected void updateScheduledJobType(ScheduledJobType scheduledJobType) {
		com.nowellpoint.api.model.document.ScheduledJobType document = modelMapper.map(scheduledJobType, com.nowellpoint.api.model.document.ScheduledJobType.class);
		replace(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, scheduledJobType);
	}
	
	protected void deleteScheduledJob(ScheduledJobType scheduledJobType) {
		com.nowellpoint.api.model.document.ScheduledJobType document = modelMapper.map(scheduledJobType, com.nowellpoint.api.model.document.ScheduledJobType.class);
		delete(document);
		hdel(encode(getSubject()), document);
	}
}
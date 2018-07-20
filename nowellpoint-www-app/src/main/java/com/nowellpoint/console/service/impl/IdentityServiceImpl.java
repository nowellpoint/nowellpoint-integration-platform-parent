package com.nowellpoint.console.service.impl;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.nowellpoint.console.entity.IdentityDAO;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.IdentityService;

public class IdentityServiceImpl extends AbstractService implements IdentityService {
	
	private IdentityDAO identityDAO;
	
	public IdentityServiceImpl() {
		identityDAO = new IdentityDAO(com.nowellpoint.console.entity.Identity.class, datastore);
	}

	@Override
	public Identity getIdentity(String id) {
		
		com.nowellpoint.console.entity.Identity entity = getEntry(id);
		
		if (entity == null) {
			
			try {
				entity = identityDAO.get(new ObjectId(id));
			} catch (IllegalArgumentException e) {
				throw new BadRequestException(String.format("Invalid Identity Id: %s", id));
			}
			
			if (entity == null) {
				throw new NotFoundException(String.format("Identity Id: %s was not found", id));
			}
			
			putEntry(entity.getId().toString(), entity);
		}
		
		return Identity.of(entity);
	}
	
	@Override
	public Identity getBySubject(String subject) {
		
		Query<com.nowellpoint.console.entity.Identity> query = identityDAO.createQuery()
				.field("subject")
				.equal(subject);
		
		com.nowellpoint.console.entity.Identity entity = identityDAO.findOne(query);
		
		putEntry(entity.getId().toString(), entity);
		
		return Identity.of(entity);
	}
}
package com.nowellpoint.console.service.impl;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;

import com.nowellpoint.console.entity.DashboardDAO;
import com.nowellpoint.console.model.Dashboard;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.DashboardService;

public class DashboardServiceImpl extends AbstractService implements DashboardService {
	
	private DashboardDAO dao;
	
	public DashboardServiceImpl() {
		dao = new DashboardDAO(com.nowellpoint.console.entity.Dashboard.class, datastore);
	}

	@Override
	public Dashboard get(String id) {
		com.nowellpoint.console.entity.Dashboard entity = getEntry(id);
		if (entity == null) {
			try {
				entity = dao.get(new ObjectId(id));
			} catch (IllegalArgumentException e) {
				throw new BadRequestException(String.format("Invalid Identity Id: %s", id));
			}
			
			if (entity == null) {
				throw new NotFoundException(String.format("Identity Id: %s was not found", id));
			}
			putEntry(entity.getId().toString(), entity);
		}
		
		return Dashboard.of(entity);
	}
}
package com.nowellpoint.console.entity;

import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "dashboards")
public class Dashboard extends BaseEntity {
	
	private static final long serialVersionUID = -6196431095732154837L;

	private Date lastRefreshedOn;
	
	public Dashboard() {
		
	}
	
	public Dashboard(ObjectId id) {
		super(id);
	}
	
	public Dashboard(String id) {
		super(id);
	}

	public Date getLastRefreshedOn() {
		return lastRefreshedOn;
	}

	public void setLastRefreshedOn(Date lastRefreshedOn) {
		this.lastRefreshedOn = lastRefreshedOn;
	}
}
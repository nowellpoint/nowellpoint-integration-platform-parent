package com.nowellpoint.aws.api.service;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Named;

import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.mongodb.AuditEntry;

@Named
@Stateless
public class AuditService {
	
	@Asynchronous
    public void onAuditEvent(@Observes AuditEntry auditEntry) {
		MongoDBDatastore.insertOne( auditEntry );
	}
}
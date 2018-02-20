package com.nowellpoint.api.rest.impl;

import java.net.URI;
import java.time.Instant;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.bson.types.ObjectId;

import com.nowellpoint.api.model.document.Lead;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.api.rest.LeadResource;
import com.nowellpoint.api.util.EnvUtil;
import com.nowellpoint.api.util.EnvUtil.Variable;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

public class LeadResourceImpl implements LeadResource {

	@Context
	private UriInfo uriInfo;
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;
	
	public Response getLead(@PathParam("id") String id) {
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Lead lead = documentManager.fetch(Lead.class, new ObjectId ( id) );
		
		return Response.ok(lead)
				.build();
		
	}

	public Response createLead(
			String leadSource,
    		String firstName,
    		String lastName,
    		String email,
    		String phone,
    		String company,
    		String message) {
		
		UserRef userRef = new UserRef(new ObjectId(EnvUtil.getValue(Variable.DEFAULT_SUBJECT)));
		
		Date now = Date.from(Instant.now());
		
		Lead lead = new Lead();
		lead.setLeadSource(leadSource);
		lead.setFirstName(firstName);
		lead.setLastName(lastName);
		lead.setEmail(email);
		lead.setDescription(message);
		lead.setCompany(company);
		lead.setPhone(phone);
		lead.setCreatedOn(now);
		lead.setCreatedBy(userRef);
		lead.setLastUpdatedOn(now);
		lead.setLastUpdatedBy(userRef);
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( lead );
		documentManager.refresh( lead );
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(LeadResource.class)
				.path("/{id}")
				.build(lead.getId().toString());
		
		return Response.created(uri)
				.build();
		
	}
}
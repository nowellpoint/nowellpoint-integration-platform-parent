package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public abstract class AbstractOrganizationService extends AbstractCacheService {

	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected void create(Organization organization) {
		MongoDocument document = organization.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		organization.replace( document );
		set( organization.getId(), document );
	}
	
	protected void delete(Organization organization) {
		MongoDocument document = organization.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(organization.getId());
	}
	
	protected void update(Organization organization) {
		MongoDocument document = organization.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		organization.replace( document );
		set( organization.getId(), document );
	}
	
	protected Organization findById(String id) {
		com.nowellpoint.api.model.document.Organization document = get(com.nowellpoint.api.model.document.Organization.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.Organization.class, new ObjectId( id ) );
			set(id, document);
		}
		
		Set<UserInfo> users = getUsers( id );
		
		Organization organization = Organization.builder()
				.from(Organization.of( document ))
				.users(users)
				.build();
		
		return organization;
	}
	
	protected Organization query(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.Organization document = documentManager.findOne( com.nowellpoint.api.model.document.Organization.class, query );
		Set<UserInfo> users = getUsers( document.getId().toString() );
		
		Organization organization = Organization.builder()
				.from(Organization.of( document ))
				.users(users)
				.build();
		
		return organization;
	}
	
	private Set<UserInfo> getUsers(String id) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Set<com.nowellpoint.api.model.document.UserProfile> documents = documentManager.find( com.nowellpoint.api.model.document.UserProfile.class, eq ( "organization", new ObjectId( id ) ) );
		Set<UserInfo> userProfiles = new HashSet<>();
		documents.stream().forEach(document -> {
			userProfiles.add( UserInfo.of(document) );
		});
		return userProfiles;
	}
}
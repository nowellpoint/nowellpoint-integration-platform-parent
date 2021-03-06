package com.nowellpoint.api.service.impl;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

abstract class AbstractUserProfileService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected void create(UserProfile userProfile) {
		MongoDocument document = userProfile.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		userProfile.replace( document );
		set( userProfile.getId(), document );
	}
	
	protected void update(UserProfile userProfile) {
		MongoDocument document = userProfile.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		userProfile.replace(document);
		set( userProfile.getId(), document );
	}
	
	protected void delete(UserProfile userProfile) {
		MongoDocument document = userProfile.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne( document );
		del( userProfile.getId() );
	}
	
	protected UserProfile findById(String id) {
		com.nowellpoint.api.model.document.UserProfile document = get(com.nowellpoint.api.model.document.UserProfile.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.UserProfile.class, new ObjectId( id ) );
			set(id, document);
		}
		UserProfile userProfile = UserProfile.of( document );
		return userProfile;
	}
	
	protected UserProfile findOne(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.UserProfile document = documentManager.findOne(com.nowellpoint.api.model.document.UserProfile.class, query );			
		UserProfile userProfile = UserProfile.of( document );
		return userProfile;
	}
	
	protected Set<UserProfile> query(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Set<com.nowellpoint.api.model.document.UserProfile> documents = documentManager.find( com.nowellpoint.api.model.document.UserProfile.class, query );
		Set<UserProfile> userProfiles = new HashSet<>();
		documents.stream().forEach(document -> {
			userProfiles.add( UserProfile.of( document ) );
		});
		return userProfiles;
	}
}
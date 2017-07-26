package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.elemMatch;

import javax.inject.Inject;

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
		userProfile.fromDocument( document );
		set( userProfile.getId(), document );
	}
	
	protected void update(UserProfile userProfile) {
		MongoDocument document = userProfile.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		userProfile.fromDocument(document);
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
	
	protected UserProfile findByIdpId(String idpId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.UserProfile document = documentManager.findOne(com.nowellpoint.api.model.document.UserProfile.class, elemMatch ( "referenceLinks", eq ( "name", idpId )));
		UserProfile userProfile = UserProfile.of( document );
		return userProfile;
	}
	
	protected UserProfile findByUsername(String username) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.UserProfile document = documentManager.findOne(com.nowellpoint.api.model.document.UserProfile.class, eq ( "username", username ) );			
		UserProfile userProfile = UserProfile.of( document );
		return userProfile;
	}
}
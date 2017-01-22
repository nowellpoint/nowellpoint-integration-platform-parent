package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.eq;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

abstract class AbstractAccountProfileService {
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;
	
	protected void create(AccountProfile accountProfile) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne(accountProfile.toDocument());
	}
	
	protected void update(AccountProfile accountProfile) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne(accountProfile.toDocument());
	}
	
	protected AccountProfile findById(String id) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.AccountProfile document = documentManager.findOne( com.nowellpoint.api.model.document.AccountProfile.class, new ObjectId( id ) );
		AccountProfile accountProfile = new AccountProfile( document );
		return accountProfile;
	}
	
	protected AccountProfile findByAccountHref(String accountHref) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.AccountProfile document = documentManager.findOne(com.nowellpoint.api.model.document.AccountProfile.class, eq ( "accountHref", accountHref ) );
		AccountProfile accountProfile = new AccountProfile( document );
		return accountProfile;
	}
	
	protected AccountProfile findByUsername(String username) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.AccountProfile document = documentManager.findOne(com.nowellpoint.api.model.document.AccountProfile.class, eq ( "username", username ) );
		AccountProfile accountProfile = new AccountProfile(document);
		return accountProfile;
	}
	
	protected AccountProfile findBySubscriptionId(String subscriptionId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.AccountProfile document = documentManager.findOne(com.nowellpoint.api.model.document.AccountProfile.class, eq ( "subscription.subscriptionId", subscriptionId ) );
		AccountProfile accountProfile = new AccountProfile(document);
		return accountProfile;
	}
}
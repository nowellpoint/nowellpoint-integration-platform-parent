package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

abstract class AbstractAccountProfileService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected void create(AccountProfile accountProfile) {
		MongoDocument document = accountProfile.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		set(accountProfile.getId(), document);
	}
	
	protected void update(AccountProfile accountProfile) {
		MongoDocument document = accountProfile.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		set(accountProfile.getId(), document);
	}
	
	protected void delete(AccountProfile accountProfile) {
		MongoDocument document = accountProfile.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(accountProfile.getId());
	}
	
	protected AccountProfile findById(String id) {
		com.nowellpoint.api.model.document.AccountProfile document = get(com.nowellpoint.api.model.document.AccountProfile.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.AccountProfile.class, new ObjectId( id ) );
			set(id, document);
		}
		AccountProfile accountProfile = new AccountProfile( document );
		return accountProfile;
	}
	
	protected AccountProfile findByAccountHref(String accountHref) {
		com.nowellpoint.api.model.document.AccountProfile document = get(com.nowellpoint.api.model.document.AccountProfile.class, accountHref);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.findOne(com.nowellpoint.api.model.document.AccountProfile.class, eq ( "accountHref", accountHref ) );
			set(accountHref, document);
		}
		AccountProfile accountProfile = new AccountProfile( document );
		return accountProfile;
	}
	
	protected AccountProfile findByUsername(String username) {
		com.nowellpoint.api.model.document.AccountProfile document = get(com.nowellpoint.api.model.document.AccountProfile.class, username);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.findOne(com.nowellpoint.api.model.document.AccountProfile.class, eq ( "username", username ) );
			set(username, document);
		}
		AccountProfile accountProfile = new AccountProfile( document );
		return accountProfile;
	}
	
	protected AccountProfile findBySubscriptionId(String subscriptionId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.AccountProfile document = documentManager.findOne(com.nowellpoint.api.model.document.AccountProfile.class, eq ( "subscription.subscriptionId", subscriptionId ) );
		AccountProfile accountProfile = new AccountProfile( document );
		return accountProfile;
	}
}
package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.eq;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.AccountProfile;
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
		documentManager.refresh( document );
		accountProfile.fromDocument(document);
		set(accountProfile.getId(), document);
	}
	
	protected void update(AccountProfile accountProfile) {
		MongoDocument document = accountProfile.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		documentManager.refresh( document );
		accountProfile.fromDocument(document);
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
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.AccountProfile document = documentManager.findOne(com.nowellpoint.api.model.document.AccountProfile.class, eq ( "accountHref", accountHref ) );
		AccountProfile accountProfile = new AccountProfile( document );
		return accountProfile;
	}
	
	protected AccountProfile findByUsername(String username) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.AccountProfile document = documentManager.findOne(com.nowellpoint.api.model.document.AccountProfile.class, eq ( "username", username ) );			
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
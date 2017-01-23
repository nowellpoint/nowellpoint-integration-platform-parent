package com.nowellpoint.api.service.impl;

import javax.inject.Inject;

import org.bson.conversions.Bson;

import com.nowellpoint.api.model.domain.SObjectDetail;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;

public class AbstractSObjectDetailService extends AbstractCacheService {

	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected void create(SObjectDetail sobjectDetail) {
		MongoDocument document = sobjectDetail.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
	}
	
	protected void update(SObjectDetail sobjectDetail) {
		MongoDocument document = sobjectDetail.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
	}
	
	protected SObjectDetail query(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.SObjectDetail document = documentManager.findOne(com.nowellpoint.api.model.document.SObjectDetail.class, query );
		SObjectDetail sobjectDetail = new SObjectDetail(document);
		return sobjectDetail;
	}
}
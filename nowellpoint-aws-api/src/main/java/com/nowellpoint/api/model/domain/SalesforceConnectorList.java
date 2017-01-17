package com.nowellpoint.api.model.domain;

import com.mongodb.client.FindIterable;

public class SalesforceConnectorList extends AbstractCollectionResource<SalesforceConnector, com.nowellpoint.api.model.document.SalesforceConnector> {
	
	public SalesforceConnectorList(FindIterable<com.nowellpoint.api.model.document.SalesforceConnector> documents) {
		super(documents);
	}

	@Override
	protected Class<SalesforceConnector> getItemType() {
		return SalesforceConnector.class;
	}
}
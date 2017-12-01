package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class SalesforceConnectorList extends DocumentCollectionResource<SalesforceConnectorOrig, com.nowellpoint.api.model.document.SalesforceConnector> {
	
	public SalesforceConnectorList(Set<com.nowellpoint.api.model.document.SalesforceConnector> documents) {
		super(documents);
	}

	@Override
	protected Class<SalesforceConnectorOrig> getItemType() {
		return SalesforceConnectorOrig.class;
	}
}
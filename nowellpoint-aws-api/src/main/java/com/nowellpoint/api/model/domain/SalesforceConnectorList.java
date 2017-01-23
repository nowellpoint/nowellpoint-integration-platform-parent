package com.nowellpoint.api.model.domain;

import java.util.Set;

public class SalesforceConnectorList extends AbstractCollectionResource<SalesforceConnector, com.nowellpoint.api.model.document.SalesforceConnector> {
	
	public SalesforceConnectorList(Set<com.nowellpoint.api.model.document.SalesforceConnector> documents) {
		super(documents);
	}

	@Override
	protected Class<SalesforceConnector> getItemType() {
		return SalesforceConnector.class;
	}
}
package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class ConnectorList extends AbstractImmutableCollectionResource<Connector, com.nowellpoint.api.model.document.Connector> {
	
	public ConnectorList(Set<com.nowellpoint.api.model.document.Connector> documents) {
		super(documents);
	}

	@Override
	protected Class<Connector> getItemType() {
		return Connector.class;
	}
}
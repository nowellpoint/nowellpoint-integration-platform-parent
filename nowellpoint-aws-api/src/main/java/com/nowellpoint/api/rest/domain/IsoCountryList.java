package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class IsoCountryList extends AbstractCollectionResource<IsoCountry, com.nowellpoint.api.model.document.IsoCountry> {
	
	public IsoCountryList(Set<com.nowellpoint.api.model.document.IsoCountry> documents) {
		super(documents);
	}

	@Override
	protected Class<IsoCountry> getItemType() {
		return IsoCountry.class;
	}
}
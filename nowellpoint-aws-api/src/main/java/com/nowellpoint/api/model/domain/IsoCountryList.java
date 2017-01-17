package com.nowellpoint.api.model.domain;

import com.mongodb.client.FindIterable;

public class IsoCountryList extends AbstractCollectionResource<IsoCountry, com.nowellpoint.api.model.document.IsoCountry> {
	
	public IsoCountryList(FindIterable<com.nowellpoint.api.model.document.IsoCountry> documents) {
		super(documents);
	}

	@Override
	protected Class<IsoCountry> getItemType() {
		return IsoCountry.class;
	}
}
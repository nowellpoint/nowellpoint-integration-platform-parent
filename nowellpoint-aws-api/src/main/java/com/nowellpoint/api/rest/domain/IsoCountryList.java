package com.nowellpoint.api.rest.domain;

import java.util.Set;

public class IsoCountryList extends ItemCollectionResource<IsoCountry> {
	
	public IsoCountryList(Set<IsoCountry> isoCountries) {
		super(isoCountries);
	}

	protected Class<IsoCountry> getItemType() {
		return IsoCountry.class;
	}
}
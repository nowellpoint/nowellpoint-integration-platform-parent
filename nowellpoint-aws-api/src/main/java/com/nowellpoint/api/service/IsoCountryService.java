package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import javax.inject.Inject;

import com.nowellpoint.api.model.document.IsoCountry;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Assert;

public class IsoCountryService extends AbstractCacheService {
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;

	public IsoCountry lookupByIso2Code(String iso2Code, String language) {
		
		IsoCountry isoCountry = get(IsoCountry.class, iso2Code);
		
		if (Assert.isNull(isoCountry)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			isoCountry = documentManager.findOne(IsoCountry.class, and ( eq ( "language", language ), eq ( "code", iso2Code ) ) );
			set(iso2Code, isoCountry);
		}
		
		return isoCountry;
	}
}
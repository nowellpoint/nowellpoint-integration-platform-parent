package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.nowellpoint.api.model.document.IsoCountry;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.document.MongoDocumentService;
import com.nowellpoint.util.Assert;

public class IsoCountryService extends AbstractCacheService {
	
	private MongoDocumentService mongoDocumentService = new MongoDocumentService();

	public IsoCountry lookupByIso2Code(String iso2Code, String language) {
		
		IsoCountry isoCountry = get(IsoCountry.class, iso2Code);
		
		if (Assert.isNull(isoCountry)) {
			isoCountry = mongoDocumentService.findOne(IsoCountry.class, and ( eq ( "language", language ), eq ( "code", iso2Code ) ) );
			set(iso2Code, isoCountry);
		}
		
		return isoCountry;
	}
}
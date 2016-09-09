package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import com.nowellpoint.api.model.document.IsoCountry;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.aws.data.mongodb.MongoDatastore;

public class IsoCountryService extends AbstractCacheService {
	
	private static final String COLLECTION_NAME = "iso.countries";

	public IsoCountry lookupByIso2Code(String iso2Code, String language) {
		
		IsoCountry isoCountry = get(IsoCountry.class, iso2Code);
		
		if (isoCountry == null) {
			MongoCollection<IsoCountry> collection = MongoDatastore.getDatabase()
					.getCollection(COLLECTION_NAME)
					.withDocumentClass(IsoCountry.class);
					
			isoCountry = collection.find( and ( eq ( "language", language ), eq ( "code", iso2Code ) ) ).first();
			
			set(iso2Code, isoCountry);
		}
		
		return isoCountry;
	}
}
package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import com.nowellpoint.aws.api.model.IsoCountry;
import com.nowellpoint.aws.data.MongoDBDatastore;

public class IsoCountryService extends AbstractCacheService {
	
	private static final String COLLECTION_NAME = "iso.countries";

	public IsoCountry lookupByIso2Code(String iso2Code, String language) {
		
		IsoCountry isoCountry = get(IsoCountry.class, iso2Code);
		
		if (isoCountry == null) {
			MongoCollection<IsoCountry> collection = MongoDBDatastore.getDatabase()
					.getCollection(COLLECTION_NAME)
					.withDocumentClass(IsoCountry.class);
					
			isoCountry = collection.find( and ( eq ( "language", language ), eq ( "code", iso2Code ) ) ).first();
			
			set(iso2Code, isoCountry);
		}
		
		return isoCountry;
	}
}
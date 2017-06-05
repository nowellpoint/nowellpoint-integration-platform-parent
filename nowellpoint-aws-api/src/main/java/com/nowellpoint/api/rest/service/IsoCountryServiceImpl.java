package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import com.nowellpoint.api.rest.domain.IsoCountry;
import com.nowellpoint.api.rest.domain.IsoCountryList;
import com.nowellpoint.api.service.IsoCountryService;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Assert;

public class IsoCountryServiceImpl extends AbstractCacheService implements IsoCountryService {
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;

	@Override
	public IsoCountry findByIso2CodeAndLanguage(String iso2Code, String language) {
		
		IsoCountry isoCountry = get(IsoCountry.class, iso2Code);
		
		if (Assert.isNull(isoCountry)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			isoCountry = documentManager.findOne(IsoCountry.class, and ( eq ( "language", language ), eq ( "iso2Code", iso2Code ) ) );
			set(iso2Code, isoCountry);
		}
		
		return isoCountry;
	}

	@Override
	public IsoCountryList findAll() {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Set<com.nowellpoint.api.model.document.IsoCountry> documents = documentManager.findAll(com.nowellpoint.api.model.document.IsoCountry.class);
		IsoCountryList isoCountryList = new IsoCountryList(documents);
		return isoCountryList;
	}

	@Override
	public IsoCountryList findByLanguage(String language) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Set<com.nowellpoint.api.model.document.IsoCountry> documents = documentManager.find(com.nowellpoint.api.model.document.IsoCountry.class, eq ( "language", language ));
		IsoCountryList isoCountryList = new IsoCountryList(documents);
		return isoCountryList;
	}
}
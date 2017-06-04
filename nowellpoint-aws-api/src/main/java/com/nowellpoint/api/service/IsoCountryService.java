package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.IsoCountry;
import com.nowellpoint.api.rest.domain.IsoCountryList;

public interface IsoCountryService {
	
	public IsoCountryList findAll();
	
	public IsoCountryList findByLanguage(String language);

	public IsoCountry findByIso2CodeAndLanguage(String iso2Code, String language);
}
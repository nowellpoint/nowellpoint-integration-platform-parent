package com.nowellpoint.aws.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IsoCountries implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8687550537429985450L;
	
	private List<IsoCountry> isoCountries;

	public IsoCountries() {
		isoCountries = new ArrayList<IsoCountry>();
	}
	
	public List<IsoCountry> getIsoCountries() {
		return isoCountries;
	}
	
	public void setIsoCountries(List<IsoCountry> isoCountries) {
		this.isoCountries = isoCountries;
	}
	
	public void add(IsoCountry isoCountry) {
		this.isoCountries.add(isoCountry);
	}
}
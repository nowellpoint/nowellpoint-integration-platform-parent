package com.nowellpoint.aws.data;

import com.nowellpoint.aws.data.mongodb.IsoCountry;

public class IsoCountryCodec extends AbstractCodec<IsoCountry> {
	
	public IsoCountryCodec() {
		super(IsoCountry.class);
	}
}
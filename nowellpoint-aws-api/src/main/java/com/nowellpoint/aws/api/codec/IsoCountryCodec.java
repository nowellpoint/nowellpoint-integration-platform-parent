package com.nowellpoint.aws.api.codec;

import com.nowellpoint.aws.api.model.IsoCountry;
import com.nowellpoint.aws.data.AbstractCodec;

public class IsoCountryCodec extends AbstractCodec<IsoCountry> {
	
	public IsoCountryCodec() {
		super(IsoCountry.class);
	}
}
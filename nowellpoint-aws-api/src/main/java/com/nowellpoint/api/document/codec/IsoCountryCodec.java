package com.nowellpoint.api.document.codec;

import com.nowellpoint.api.model.IsoCountry;
import com.nowellpoint.aws.data.AbstractCodec;

public class IsoCountryCodec extends AbstractCodec<IsoCountry> {
	
	public IsoCountryCodec() {
		super(IsoCountry.class);
	}
}
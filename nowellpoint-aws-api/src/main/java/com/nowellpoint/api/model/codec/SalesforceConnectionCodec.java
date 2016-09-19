package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.SalesforceConnector;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class SalesforceConnectionCodec extends AbstractCodec<SalesforceConnector> {

	public SalesforceConnectionCodec() {
		super(SalesforceConnector.class);
	}
}
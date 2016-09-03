package com.nowellpoint.api.document.codec;

import com.nowellpoint.api.model.SalesforceConnector;
import com.nowellpoint.aws.data.AbstractCodec;

public class SalesforceConnectionCodec extends AbstractCodec<SalesforceConnector> {

	public SalesforceConnectionCodec() {
		super(SalesforceConnector.class);
	}
}
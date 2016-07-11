package com.nowellpoint.aws.api.codec;

import com.nowellpoint.aws.api.model.SalesforceConnector;
import com.nowellpoint.aws.data.AbstractCodec;

public class SalesforceConnectionCodec extends AbstractCodec<SalesforceConnector> {

	public SalesforceConnectionCodec() {
		super(SalesforceConnector.class);
	}
}
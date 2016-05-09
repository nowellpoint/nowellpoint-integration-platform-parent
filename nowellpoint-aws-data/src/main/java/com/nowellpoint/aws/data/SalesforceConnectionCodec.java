package com.nowellpoint.aws.data;

import com.nowellpoint.aws.data.mongodb.SalesforceConnector;

public class SalesforceConnectionCodec extends AbstractCodec<SalesforceConnector> {

	public SalesforceConnectionCodec() {
		super(SalesforceConnector.class);
	}
}
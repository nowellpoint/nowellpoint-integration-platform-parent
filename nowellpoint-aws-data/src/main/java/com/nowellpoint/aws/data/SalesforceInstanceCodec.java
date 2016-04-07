package com.nowellpoint.aws.data;

import com.nowellpoint.aws.data.mongodb.SalesforceConnector;

public class SalesforceInstanceCodec extends AbstractCodec<SalesforceConnector> {

	public SalesforceInstanceCodec() {
		super(SalesforceConnector.class);
	}
}
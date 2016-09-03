package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.SalesforceConnectorDocument;
import com.nowellpoint.aws.data.AbstractCodec;

public class SalesforceConnectionCodec extends AbstractCodec<SalesforceConnectorDocument> {

	public SalesforceConnectionCodec() {
		super(SalesforceConnectorDocument.class);
	}
}
package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.SObjectDescription;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class SObjectDescriptionCodec extends AbstractCodec<SObjectDescription> {

	public SObjectDescriptionCodec() {
		super(SObjectDescription.class);
	}
}
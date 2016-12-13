package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.SObjectDetail;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class SObjectDetailCodec extends AbstractCodec<SObjectDetail> {

	public SObjectDetailCodec() {
		super(SObjectDetail.class);
	}
}
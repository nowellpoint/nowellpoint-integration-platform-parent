package com.nowellpoint.aws.api.codec;

import com.nowellpoint.aws.api.model.AuditHistory;
import com.nowellpoint.aws.data.AbstractCodec;

public class AuditHistoryCodec extends AbstractCodec<AuditHistory> {

	public AuditHistoryCodec() {
		super(AuditHistory.class);
	}
}
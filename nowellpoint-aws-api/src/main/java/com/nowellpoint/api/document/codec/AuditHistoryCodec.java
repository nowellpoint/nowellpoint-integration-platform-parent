package com.nowellpoint.api.document.codec;

import com.nowellpoint.api.model.AuditHistory;
import com.nowellpoint.aws.data.AbstractCodec;

public class AuditHistoryCodec extends AbstractCodec<AuditHistory> {

	public AuditHistoryCodec() {
		super(AuditHistory.class);
	}
}
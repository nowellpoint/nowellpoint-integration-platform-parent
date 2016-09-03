package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.AuditHistory;
import com.nowellpoint.aws.data.AbstractCodec;

public class AuditHistoryCodec extends AbstractCodec<AuditHistory> {

	public AuditHistoryCodec() {
		super(AuditHistory.class);
	}
}
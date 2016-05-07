package com.nowellpoint.aws.data;

import com.nowellpoint.aws.data.mongodb.AuditHistory;

public class AuditHistoryCodec extends AbstractCodec<AuditHistory> {

	public AuditHistoryCodec() {
		super(AuditHistory.class);
	}
}
package com.nowellpoint.aws.data;

import com.nowellpoint.aws.data.mongodb.AuditEntry;

public class AuditEntryCodec extends AbstractCodec<AuditEntry> {

	public AuditEntryCodec() {
		super(AuditEntry.class);
	}
}
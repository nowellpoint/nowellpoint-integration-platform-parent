package com.nowellpoint.aws.api.codec;

import com.nowellpoint.aws.api.model.Application;
import com.nowellpoint.aws.data.AbstractCodec;

public class BackupHistoryCodec extends AbstractCodec<Application> {
	
	public BackupHistoryCodec() {
		super(Application.class);
	}
}
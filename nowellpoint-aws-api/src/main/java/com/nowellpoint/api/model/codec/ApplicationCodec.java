package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ApplicationDocument;
import com.nowellpoint.aws.data.AbstractCodec;

public class ApplicationCodec extends AbstractCodec<ApplicationDocument> {
	
	public ApplicationCodec() {
		super(ApplicationDocument.class);
	}
}
package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.AccountProfileDocument;
import com.nowellpoint.aws.data.AbstractCodec;

public class AccountProfileCodec extends AbstractCodec<AccountProfileDocument> {

	public AccountProfileCodec() {
		super(AccountProfileDocument.class);
	}
}
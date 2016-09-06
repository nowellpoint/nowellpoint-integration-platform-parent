package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.AccountProfile;
import com.nowellpoint.aws.data.AbstractCodec;

public class AccountProfileCodec extends AbstractCodec<AccountProfile> {

	public AccountProfileCodec() {
		super(AccountProfile.class);
	}
}
package com.nowellpoint.api.document.codec;

import com.nowellpoint.api.model.AccountProfile;
import com.nowellpoint.aws.data.AbstractCodec;

public class AccountProfileCodec extends AbstractCodec<AccountProfile> {

	public AccountProfileCodec() {
		super(AccountProfile.class);
	}
}
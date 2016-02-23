package com.nowellpoint.aws.data;

import com.nowellpoint.aws.data.mongodb.Identity;

public class IdentityCodec extends AbstractCodec<Identity> {

	public IdentityCodec() {
		super(Identity.class);
	}
}
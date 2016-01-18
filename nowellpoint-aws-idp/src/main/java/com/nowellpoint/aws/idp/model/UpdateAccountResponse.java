package com.nowellpoint.aws.idp.model;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public class UpdateAccountResponse extends AbstractLambdaResponse {
	
	private static final long serialVersionUID = 7454196046925853087L;
	private Account account;
	
	public UpdateAccountResponse() {
		
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
}
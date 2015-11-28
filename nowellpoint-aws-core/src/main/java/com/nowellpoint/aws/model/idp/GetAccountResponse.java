package com.nowellpoint.aws.model.idp;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public class GetAccountResponse extends AbstractLambdaResponse {
	
	private static final long serialVersionUID = 7454196046925853087L;
	private Account account;
	
	public GetAccountResponse() {
		
	}
	
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
}
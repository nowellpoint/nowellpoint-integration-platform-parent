package com.nowellpoint.www.app.util;

import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;

import spark.Request;

public class RequestWrapper {
	
	private Request request;
	
	public RequestWrapper(Request request) {
		this.request = request;
	}

	public Token getToken() {
		Token token = request.attribute("token");
		return token;
	}
	
	public Account getAccount() {
		Account account = request.attribute("account");
		return account;
	}
	
	public String getBodyFromQueryParams() {
		StringBuilder sb = new StringBuilder();
		request.queryParams().stream().forEach(p-> {
			if (! request.queryParams(p).isEmpty()) {
				sb.append(p);
				sb.append("=");
				sb.append(request.queryParams(p));
				sb.append("&");
			}
		});
		return sb.toString();
	}
}
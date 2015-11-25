package com.nowellpoint.aws.tools.model;

public class Function {

	private Lambda configuration;
	
	private Code code;
	
	public Function() {
		
	}

	public Lambda getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Lambda configuration) {
		this.configuration = configuration;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}
}
package com.nowellpoint.aws.idp.lambda;

public class Function {

	private Configuration configuration;
	
	private Code code;
	
	public Function() {
		
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}
}
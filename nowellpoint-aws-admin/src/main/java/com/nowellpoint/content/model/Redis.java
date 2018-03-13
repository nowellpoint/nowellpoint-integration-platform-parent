package com.nowellpoint.content.model;

public class Redis {
	
	private String password;
	private String host;
	private Integer port;
	private String encryptionKey;
	
	public Redis() {
		
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}
}
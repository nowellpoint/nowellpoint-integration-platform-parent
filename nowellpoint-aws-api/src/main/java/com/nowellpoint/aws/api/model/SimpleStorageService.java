package com.nowellpoint.aws.api.model;

public class SimpleStorageService {
	
	private String bucketName;
	
	private String awsAccessKey;
	
	private String awsSecretAccessKey;
	
	public SimpleStorageService() {
		
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}

	public String getAwsSecretAccessKey() {
		return awsSecretAccessKey;
	}

	public void setAwsSecretAccessKey(String awsSecretAccessKey) {
		this.awsSecretAccessKey = awsSecretAccessKey;
	}
}
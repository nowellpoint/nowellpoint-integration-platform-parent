package com.nowellpoint.aws.tools.model;

public class Code {

	private String s3Bucket;
	
	private String s3Key;
	
	private String s3ObjectVersion;
	
	public Code() {
		
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}

	public String getS3Key() {
		return s3Key;
	}

	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

	public String getS3ObjectVersion() {
		return s3ObjectVersion;
	}

	public void setS3ObjectVersion(String s3ObjectVersion) {
		this.s3ObjectVersion = s3ObjectVersion;
	}
}
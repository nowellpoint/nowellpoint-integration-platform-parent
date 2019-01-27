package com.nowellpoint.listener.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class S3 {
	private String s3SchemaVersion;
	private String configurationId;
	private Bucket bucket;
	private Object object;
}
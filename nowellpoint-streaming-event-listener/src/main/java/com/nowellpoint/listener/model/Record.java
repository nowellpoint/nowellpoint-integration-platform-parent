package com.nowellpoint.listener.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Record {
	private String eventVersion;
	private String eventSource;
	private String awsRegion;
	private String eventTime;
	private String eventName;
	private S3 s3;
}

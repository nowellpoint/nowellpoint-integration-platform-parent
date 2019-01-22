package com.nowellpoint.listener.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {
	private String eventVersion;
	private String eventSource;
	private String awsRegion;
	private String eventTime;
	private String eventName;
	private S3 s3;
}

package com.nowellpoint.listener.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class S3 {
	private String s3SchemaVersion;
	private String configurationId;
	private Bucket bucket;
	private Object object;
}
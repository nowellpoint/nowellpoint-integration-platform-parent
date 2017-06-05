/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.api.model.document;

import java.time.Instant;
import java.util.Date;

public class JobOutput {
	
	private String type;
	
	private String filename;
	
	private Long filesize;
	
	private String bucket;
	
	private String key;
	
	private Date addedOn;
	
	private JobOutput() {
		
	}
	
	private JobOutput(String type, String filename, Long filesize, String bucket, String key) {
		this.type = type;
		this.filename = filename;
		this.filesize = filesize;
		this.bucket = bucket;
		this.key = key;
		this.addedOn = Date.from(Instant.now());
	}
	
	public static JobOutput of(String type, Long filesize, String bucket, String key) {
		return new JobOutput(type, key.substring(key.indexOf("/") + 1).concat(".json"), filesize, bucket, key);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Long getFilesize() {
		return filesize;
	}

	public void setFilesize(Long filesize) {
		this.filesize = filesize;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}
}
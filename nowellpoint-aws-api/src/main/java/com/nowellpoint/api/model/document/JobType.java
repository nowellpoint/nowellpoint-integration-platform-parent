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

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.EmbedOne;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="job.types")
public class JobType extends MongoDocument {

	private static final long serialVersionUID = -5368597023270502005L;
	
	@Reference(referenceClass = UserProfile.class)
	private UserRef createdBy;
	
	@Reference(referenceClass = UserProfile.class)
	private UserRef lastUpdatedBy;
	
	private String name;
	
	private String group;
	
	private String code;
	
	private String className;
	
	private String description;
	
	private String languageSidKey;
	
	private String template;
	
	private String href;
	
	@EmbedOne
	private ConnectorType source;
	
	@EmbedOne
	private ConnectorType target;
	
	public JobType() {
		
	}

	public UserRef getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserRef createdBy) {
		this.createdBy = createdBy;
	}

	public UserRef getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserRef lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguageSidKey() {
		return languageSidKey;
	}

	public void setLanguageSidKey(String languageSidKey) {
		this.languageSidKey = languageSidKey;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public ConnectorType getSource() {
		return source;
	}

	public void setSource(ConnectorType source) {
		this.source = source;
	}

	public ConnectorType getTarget() {
		return target;
	}

	public void setTarget(ConnectorType target) {
		this.target = target;
	}
}
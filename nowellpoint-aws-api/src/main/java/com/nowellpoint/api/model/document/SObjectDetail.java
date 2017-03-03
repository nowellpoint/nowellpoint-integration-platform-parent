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

import org.bson.types.ObjectId;

import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="sobject.details")
public class SObjectDetail extends MongoDocument {

	private static final long serialVersionUID = 8161621113389201360L;
	
	@Reference(referenceClass = AccountProfile.class)
	private UserRef createdBy;
	
	@Reference(referenceClass = AccountProfile.class)
	private UserRef lastUpdatedBy;
	
	private ObjectId connectorId;
	
	private String instanceKey;
	
	private String name;
	
	private Long totalSize;
	
	private DescribeSobjectResult result;
	
	public SObjectDetail() {
		
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

	public ObjectId getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(ObjectId connectorId) {
		this.connectorId = connectorId;
	}

	public String getInstanceKey() {
		return instanceKey;
	}

	public void setInstanceKey(String instanceKey) {
		this.instanceKey = instanceKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	public DescribeSobjectResult getResult() {
		return result;
	}

	public void setResult(DescribeSobjectResult result) {
		this.result = result;
	}
}
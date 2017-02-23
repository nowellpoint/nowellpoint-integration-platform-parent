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

import java.util.List;

import org.bson.types.ObjectId;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.EmbedMany;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="projects")
public class Project extends MongoDocument {

	private static final long serialVersionUID = 2884164327750192485L;
	
	private String name;
	
	private String description;
	
	private String stage;
	
	@Reference(collectionName="account.profiles")
	private UserRef owner;
	
	@EmbedMany
	private List<Application> salesforceOrganizations;

	public Project() {
		
	}
	
	public Project(ObjectId id) {
		setId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public UserRef getOwner() {
		return owner;
	}

	public void setOwner(UserRef owner) {
		this.owner = owner;
	}
	
	public List<Application> getSalesforceOrganizations() {
		return salesforceOrganizations;
	}
	
	public void setSalesforceOrganizations(List<Application>salesforceOrganizations) {
		this.salesforceOrganizations = salesforceOrganizations;
	}
}
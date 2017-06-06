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

package com.nowellpoint.client;

import org.immutables.value.Value;

import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.resource.AccountProfileResource;
import com.nowellpoint.client.resource.IdentityResource;
import com.nowellpoint.client.resource.JobResource;
import com.nowellpoint.client.resource.JobTypeResource;
import com.nowellpoint.client.resource.PlanResource;
import com.nowellpoint.client.resource.SalesforceConnectorResource;
import com.nowellpoint.client.resource.SalesforceResource;

@Value.Immutable
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public abstract class NowellpointClient  {
	abstract Token token();
	
	public static Builder builder() {
		return ImmutableNowellpointClient.builder();
	}
	
	public static NowellpointClient defaultClient(Token token) {
		return ImmutableNowellpointClient.builder()
				.token(token)
				.build();
	}
	
	public interface Builder {
		Builder token(Token token);
		NowellpointClient build();
	}
	
	public IdentityResource identity() {
		return new IdentityResource(token());
	}
	
	public PlanResource plan() {
		return new PlanResource(token());
	}
	
	public JobResource job() {
		return new JobResource(token());
	}
	
	public SalesforceConnectorResource salesforceConnector() {
		return new SalesforceConnectorResource(token());
	}
	
	public JobTypeResource scheduledJobType() {
		return new JobTypeResource(token());
	}
	
	public AccountProfileResource accountProfile() {
		return new AccountProfileResource(token());
	}
	
	public SalesforceResource salesforce() {
		return new SalesforceResource(token());
	}
}
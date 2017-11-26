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

package com.nowellpoint.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.api.rest.domain.ErrorOrig;

import com.okta.sdk.resource.ResourceException;

@Provider
public class ResourceExceptionMapper implements ExceptionMapper<ResourceException> {

	@Override
	public Response toResponse(ResourceException exception) {		
		ErrorOrig errorOrig = new ErrorOrig(
				exception.getCode(),
				exception.getCauses() != null ?
				exception.getCauses().get(0).getSummary() :
					exception.getMessage());
		
		ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
		builder.entity(errorOrig);
		return builder.build();
	}
}
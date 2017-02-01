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

import com.nowellpoint.api.model.domain.Error;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;

@Provider
public class DocumentNotFoundExceptionMapper implements ExceptionMapper<DocumentNotFoundException> {

	@Override
	public Response toResponse(DocumentNotFoundException exception) {
		Error error = new Error(3000, exception.getMessage());
		ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
		builder.entity(error);
		return builder.build();
	}
}
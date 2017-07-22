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

package com.nowellpoint.api.rest.domain;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1758285959718030858L;
	
	private List<String> messages;
	
	public ValidationException(String message) {
		super(message);
		this.messages = new ArrayList<>();
		this.messages.add(message);
	}
	
	public ValidationException(List<String> messages) {
		super(String.join(" ", messages));
		this.messages = messages;
	}

	public String getCode() {
		return "VALIDATION";
	}
	
	public List<String> getMessages() {
		return messages;
	}
}
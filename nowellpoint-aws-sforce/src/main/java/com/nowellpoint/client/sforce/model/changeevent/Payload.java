package com.nowellpoint.client.sforce.model.changeevent;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Payload implements Serializable {

	private static final long serialVersionUID = -2457215427134346117L;
	
	@Getter @JsonProperty(value="LastModifiedDate") private Date lastModifiedDate;
	@Getter @JsonProperty(value="ChangeEventHeader") private ChangeEventHeader changeEventHeader;
	@Getter Map<String, Object> attributes;
	 
    @JsonAnySetter
    void setAttribute(String key, Object value) {
    	if (attributes == null) {
    		attributes = new LinkedHashMap<>();
    	}
    	attributes.put(key, value);
    }
}
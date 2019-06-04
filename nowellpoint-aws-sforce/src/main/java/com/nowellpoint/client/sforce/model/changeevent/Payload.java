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
	@Getter Map<String, Object> changedFields;
	 
    @JsonAnySetter
    void setDetail(String key, Object value) {
    	if (changedFields == null) {
    		changedFields = new LinkedHashMap<>();
    	}
    	changedFields.put(key, value);
    }
}
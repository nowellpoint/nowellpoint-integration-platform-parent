package com.nowellpoint.client.sforce.model.changeevent;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ChangeEventHeader implements Serializable {

	private static final long serialVersionUID = 8008269330066701272L;
	
	@Getter @JsonProperty(value="commitNumber") private Long commitNumber;
	@Getter @JsonProperty(value="commitUser") private String commitUser;
	@Getter @JsonProperty(value="sequenceNumber") private Integer sequenceNumber;
	@Getter @JsonProperty(value="entityName") private String entityName;
	@Getter @JsonProperty(value="changeType") private String changeType;
	@Getter @JsonProperty(value="changeOrigin") private String changeOrigin;
	@Getter @JsonProperty(value="transactionKey") private String transactionKey;
	@Getter @JsonProperty(value="commitTimestamp") private Long commitTimestamp;
	@Getter @JsonProperty(value="recordIds") private List<String> recordIds;
}
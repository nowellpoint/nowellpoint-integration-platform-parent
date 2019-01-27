package com.nowellpoint.listener.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Payload implements Serializable {
	private static final long serialVersionUID = 909294436781871346L;
	private String id;
	private String name;
	private Date createdDate;
	private String createdById;
	private Date lastModifiedDate;
	private String lastModifiedById;
}
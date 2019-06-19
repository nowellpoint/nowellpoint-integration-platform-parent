package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.nowellpoint.client.sforce.annotation.Column;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Attributes implements Serializable {
	
	private static final long serialVersionUID = -4180438562996624005L;

	@Getter @Column private String type;
	@Getter @Column private String url;
}
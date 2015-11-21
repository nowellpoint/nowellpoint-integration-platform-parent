package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

public class Notification implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = -6669913219066631132L;

	/**
	 * 
	 */

	private String outboundMessageId;

	/**
	 * 
	 */

	private SObject sobject;

	public Notification() {

	}

	public String getOutboundMessageId() {
		return outboundMessageId;
	}

	public void setOutboundMessageId(String outboundMessageId) {
		this.outboundMessageId = outboundMessageId;
	}

	public SObject getSobject() {
		return sobject;
	}

	public void setSobject(SObject sobject) {
		this.sobject = sobject;
	}
}
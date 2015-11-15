package com.nowellpoint.aws.sforce.model;

import java.io.Serializable;
import java.util.List;

public class OutboundMessage implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = -7684111059843154971L;

	/**
	 * 
	 */

	private String organizationId;

	/**
	 * 
	 */

	private String actionId;

	/**
	 * 
	 */

	private String sessionId;

	/**
	 * 
	 */

	private String enterpriseUrl;

	/**
	 * 
	 */

	private String partnerUrl;

	/**
	 * 
	 */

	private List<Notification> notifications;


	public OutboundMessage() {

	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public String getEnterpriseUrl() {
		return enterpriseUrl;
	}

	public void setEnterpriseUrl(String enterpriseUrl) {
		this.enterpriseUrl = enterpriseUrl;
	}

	public String getPartnerUrl() {
		return partnerUrl;
	}

	public void setPartnerUrl(String partnerUrl) {
		this.partnerUrl = partnerUrl;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}
}
package com.nowellpoint.console.entity;

import java.util.Set;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "organizations")
public class Organization extends BaseEntity {
	
	private static final long serialVersionUID = -7891914413636330372L;

	private String number;
	
	private String name;
	
	private String domain;
	
	private String organizationType;
	
	private Address address;
	
	private Connection connection;
	
	private Dashboard dashboard;
	
	private Subscription subscription;
	
	private Set<StreamingEventListener> streamingEventListeners;
	
	private Set<Notification> notifications;
	
	public Organization() {
		
	}

	public Organization(String id) {
		super(id);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Dashboard getDashboard() {
		return dashboard;
	}

	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Set<StreamingEventListener> getStreamingEventListeners() {
		return streamingEventListeners;
	}

	public void setStreamingEventListeners(Set<StreamingEventListener> streamingEventListeners) {
		this.streamingEventListeners = streamingEventListeners;
	}

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}
}
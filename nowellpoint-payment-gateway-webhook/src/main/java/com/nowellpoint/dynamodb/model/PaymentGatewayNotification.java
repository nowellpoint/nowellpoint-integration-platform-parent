package com.nowellpoint.dynamodb.model;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotEncrypt;

@DynamoDBTable(tableName="PAYMENT_GATEWAY_NOTIFICATIONS")
public class PaymentGatewayNotification {
	
	public enum Status {
		RECEIVED,
		PROCESSED,
		FAILED,
		PROCESSING
	}
	
	private String id;
	
	private String environment;
	
	private String merchantId;
	
	private String publicKey;
	
	private String privateKey;
	
	private String subscriptionId;
	
	private String status;
	
	private Date receivedOn;
	
	private String webhookNotificationKind;
	
	private String emailApiKey;
	
	private String applicationHostname;
	
	private String errorMessage;
	
	public PaymentGatewayNotification() {
		
	}

	@DynamoDBHashKey(attributeName="ID")
	@DynamoDBAutoGeneratedKey
	public String getId() { 
		return id; 
	}

	public void setId(String id) { 
		this.id = id; 
	}

	@DoNotEncrypt
	@DynamoDBAttribute(attributeName="ENVIRONMENT")  
	public String getEnvironment() { 
		return environment; 
	}

	public void setEnvironment(String environment) { 
		this.environment = environment; 
	}

	@DynamoDBAttribute(attributeName="MERCHANT_ID")  
	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	@DynamoDBAttribute(attributeName="PUBLIC_KEY")  
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@DynamoDBAttribute(attributeName="PRIVATE_KEY")  
	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	@DoNotEncrypt
	@DynamoDBAttribute(attributeName="SUBSCRIPTION_ID")  
	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	@DoNotEncrypt
	@DynamoDBAttribute(attributeName="RECEIVED_ON")  
	public Date getReceivedOn() {
		return receivedOn;
	}

	public void setReceivedOn(Date receivedOn) {
		this.receivedOn = receivedOn;
	}

	@DoNotEncrypt
	@DynamoDBAttribute(attributeName="STATUS")  
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@DoNotEncrypt
	@DynamoDBAttribute(attributeName="WEBHOOK_NOTIFICATION_KIND") 
	public String getWebhookNotificationKind() {
		return webhookNotificationKind;
	}

	public void setWebhookNotificationKind(String webhookNotificationKind) {
		this.webhookNotificationKind = webhookNotificationKind;
	}
	
	@DynamoDBAttribute(attributeName="EMAIL_API_KEY")  
	public String getEmailApiKey() {
		return emailApiKey;
	}

	public void setEmailApiKey(String emailApiKey) {
		this.emailApiKey = emailApiKey;
	}

	@DoNotEncrypt
	@DynamoDBAttribute(attributeName="APPLICATION_HOSTNAME") 
	public String getApplicationHostname() {
		return applicationHostname;
	}

	public void setApplicationHostname(String applicationHostname) {
		this.applicationHostname = applicationHostname;
	}

	@DoNotEncrypt
	@DynamoDBAttribute(attributeName="ERROR_MESSAGE")
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public PaymentGatewayNotification environment(String environment) {
		setEnvironment(environment);
		return this;
	}
	
	public PaymentGatewayNotification merchantId(String merchantId) {
		setMerchantId(merchantId);
		return this;
	}
	
	public PaymentGatewayNotification privateKey(String privateKey) {
		setPrivateKey(privateKey);
		return this;
	}
	
	public PaymentGatewayNotification publicKey(String publicKey) {
		setPublicKey(publicKey);
		return this;
	}
	
	public PaymentGatewayNotification subscriptionId(String subscriptionId) {
		setSubscriptionId(subscriptionId);
		return this;
	}
	
	public PaymentGatewayNotification receivedOn(Date receivedOn) {
		setReceivedOn(receivedOn);
		return this;
	}
	
	public PaymentGatewayNotification webhookNotificationKind(String webhookNotificationKind) {
		setWebhookNotificationKind(webhookNotificationKind);
		return this;
	}
	
	public PaymentGatewayNotification emailApiKey(String emailApiKey) {
		setEmailApiKey(emailApiKey);
		return this;
	}
	
	public PaymentGatewayNotification status(String status) {
		setStatus(status);
		return this;
	}
	
	public PaymentGatewayNotification applicationHostname(String applicationHostname) {
		setApplicationHostname(applicationHostname);
		return this;
	}
}
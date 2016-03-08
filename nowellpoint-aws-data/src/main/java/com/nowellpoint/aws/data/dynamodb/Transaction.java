package com.nowellpoint.aws.data.dynamodb;

import java.util.Date;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;

@DynamoDBTable(tableName="Transactions")
public class Transaction {
	
	public enum TransactionStatus {
		NEW,
		COMPLETE,
		ERROR
	};
	
	public enum TransactionType {
		OUTBOUND_MESSAGE
	};

	@DynamoDBAutoGeneratedKey
	@DynamoDBHashKey(attributeName="Id")  
	private String id;
	
	@DynamoDBAttribute(attributeName="Status")  
	private String status;
	
	@DynamoDBAttribute(attributeName="TransactionDate")  
	private Date transactionDate;
	
	@DynamoDBAttribute(attributeName="Type")  
	private String type;
	
	@DynamoDBAttribute(attributeName="OrganizationId")  
	private String organizationId;
	
	@DynamoDBAttribute(attributeName="UserId")  
	private String userId;
	
	@DynamoDBAttribute(attributeName="Payload")
	private String payload;
	
	@DynamoDBAttribute(attributeName="RecordCount")
	private Integer recordCount;
	
	@DynamoDBAttribute(attributeName="ExecutionTime")
	private Long executionTime;
	
	@DynamoDBAttribute(attributeName="ErrorMessage")
	private String errorMessage;
	
	public Transaction() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	public Long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
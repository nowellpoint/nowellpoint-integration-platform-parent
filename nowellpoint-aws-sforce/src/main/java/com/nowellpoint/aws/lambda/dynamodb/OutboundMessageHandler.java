package com.nowellpoint.aws.lambda.dynamodb;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.util.Base64;
import com.nowellpoint.aws.model.sforce.OutboundMessage;
import com.nowellpoint.aws.model.sforce.Notification;

public class OutboundMessageHandler {
	
	private static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient());
	private static AWSKMS kms = new AWSKMSClient();

	public String handleEvent(DynamodbEvent event, Context context) {
		context.getLogger().log("DynamodbEvent received");
		event.getRecords().forEach(record -> {
			record.getDynamodb().getKeys().forEach( (key, value) -> {
				System.out.println("key: " + key);
				System.out.println("value: " + value.getS());
				
				PrimaryKey primaryKey = new PrimaryKey("OutboundMessageId", value.getS());
				
				Table table = dynamoDB.getTable("OutboundMessages");
				
				Item item = table.getItem(primaryKey);
				String payload = item.getString("Payload");
				
				ByteBuffer ciphertext = ByteBuffer.wrap(Base64.decode(payload.getBytes()));
				DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(ciphertext);
				ByteBuffer plainText = kms.decrypt(decryptRequest).getPlaintext();
				
				System.out.println(new String(plainText.array(), Charset.forName("UTF-8")));
				
			});
		});
		
		return context.getAwsRequestId();
	}
}

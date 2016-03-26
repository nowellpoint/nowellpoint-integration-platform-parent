package com.nowellpoint.aws.event;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.nowellpoint.aws.event.model.Signup;
import com.nowellpoint.aws.idp.model.Account;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventBuilder;
import com.nowellpoint.aws.data.dynamodb.EventStatus;
import com.nowellpoint.aws.data.mongodb.Address;
import com.nowellpoint.aws.data.mongodb.Identity;
import com.nowellpoint.aws.model.sforce.Lead;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class SignUpEventHandler implements AbstractEventHandler {
	
	private static LambdaLogger logger;
	
	private static DynamoDBMapper mapper;

	@Override
	public void process(Event event, Map<String,String> properties, Context context) throws Exception {
		
		logger = context.getLogger();
		
		mapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		logger.log(this.getClass().getName() + " Starting SignUpEventHandler");
		
		Signup signup = objectMapper.readValue(event.getPayload(), Signup.class);
		
		Lead lead = new Lead();
		lead.setLeadSource(signup.getLeadSource());
		lead.setFirstName(signup.getFirstName());
		lead.setLastName(signup.getLastName());
		lead.setEmail(signup.getEmail());
		lead.setPhone(signup.getPhone());
		lead.setCompany(signup.getCompany());
		lead.setTitle(signup.getTitle());
		lead.setCountryCode(signup.getCountryCode());
		
		Account account = new Account();
		account.setGivenName(signup.getFirstName());
		account.setMiddleName(null);
		account.setSurname(signup.getLastName());
		account.setEmail(signup.getEmail());
		account.setUsername(signup.getEmail());
		account.setPassword(signup.getPassword());
		account.setStatus("UNVERIFIED");
		
		Identity identity = new Identity();
		identity.setCreatedById(event.getSubject());
		identity.setLastModifiedById(event.getSubject());
		identity.setFirstName(signup.getFirstName());
		identity.setLastName(signup.getLastName());
		identity.setEmail(signup.getEmail());
		identity.setCompany(signup.getCompany());
		identity.setTitle(signup.getTitle());
		identity.setPhone(signup.getPhone());
		identity.setUsername(signup.getEmail());
		identity.setIsActive(Boolean.TRUE);
		
		Address address = new Address();
		address.setCountryCode(signup.getCountryCode());
		
		identity.setAddress(address);

		try {			
			insertEvent(event, lead, EventAction.LEAD);
			insertEvent(event, account, EventAction.ACCOUNT);
			insertEvent(event, identity, EventAction.IDENTITY);
			event.setEventStatus(EventStatus.COMPLETE.toString());	
		} catch (JsonProcessingException e) {
			logger.log( "Signup Exception: " + e.getMessage());
			event.setEventStatus(EventStatus.ERROR.toString());
			event.setErrorMessage(e.getMessage());
		}
		
		event.setProcessedDate(Date.from(Instant.now()));
		event.setExecutionTime(System.currentTimeMillis() - event.getStartTime());
	}
	
	private void insertEvent(Event parent, Object object, EventAction eventAction) throws JsonProcessingException {
		Event event = new EventBuilder()
				.withSubject(parent.getSubject())
				.withEventAction(eventAction)
				.withEventSource(this.getClass().getName())
				.withPropertyStore(parent.getPropertyStore())
				.withParentEventId(parent.getId())
				.withPayload(object)
				.withType(object.getClass())
				.build();
			
		mapper.save(event);
	}
}
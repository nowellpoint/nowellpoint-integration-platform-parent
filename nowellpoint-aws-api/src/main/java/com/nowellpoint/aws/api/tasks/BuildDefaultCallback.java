package com.nowellpoint.aws.api.tasks;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import com.nowellpoint.aws.api.model.EventListener;
import com.nowellpoint.aws.api.model.dynamodb.Callback;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.InvalidSObjectFault;
import com.sforce.ws.ConnectionException;

public class BuildDefaultCallback implements Callable<Callback> {
	
	private final PartnerConnection connection;
	private final EventListener eventListener;
	
	public BuildDefaultCallback(final PartnerConnection connection, final EventListener eventListener) {
		this.connection = connection;
		this.eventListener = eventListener;
	}

	@Override
	public Callback call() throws Exception {
		
		Callback callback = new Callback();
		callback.setType(eventListener.getName());
		callback.setCreate(eventListener.getCreate());
		callback.setUpdate(eventListener.getUpdate());
		callback.setDelete(eventListener.getDelete());

		try {
			
			DescribeSObjectResult result = connection.describeSObject(eventListener.getName());
			
			Field[] fields = result.getFields();
			
			String queryString = "Select %s From ".concat(eventListener.getName());
			
			queryString = String.format(queryString, Arrays.asList(fields)
					.stream()
					.map(field -> field.getName())
					.collect(Collectors.joining(", ")));
			
			callback.setQueryString(queryString);

		} catch (ConnectionException e) {
			if (e instanceof InvalidSObjectFault) {
				InvalidSObjectFault fault = (InvalidSObjectFault) e;
				throw new BadRequestException(fault.getExceptionCode().name().concat(": ").concat(fault.getExceptionMessage()));
			} else {
				throw new InternalServerErrorException(e.getMessage());
			}
		}
		
		return callback;
	}
}
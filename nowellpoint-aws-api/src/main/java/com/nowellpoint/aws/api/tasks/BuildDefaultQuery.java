package com.nowellpoint.aws.api.tasks;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import com.nowellpoint.aws.api.model.dynamodb.Query;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.InvalidSObjectFault;
import com.sforce.ws.ConnectionException;

public class BuildDefaultQuery implements Callable<Query> {
	
	private final PartnerConnection connection;
	private final String sobject;
	
	public BuildDefaultQuery(final PartnerConnection connection, final String sobject) {
		this.connection = connection;
		this.sobject = sobject;
	}

	@Override
	public Query call() throws Exception {
		
		Query query = new Query();

		try {
			
			DescribeSObjectResult result = connection.describeSObject(sobject);
			
			Field[] fields = result.getFields();
			
			String queryString = "Select %s From ".concat(sobject);
			
			queryString = String.format(queryString, Arrays.asList(fields)
					.stream()
					.map(field -> field.getName())
					.collect(Collectors.joining(", ")));
			
			query.setType(sobject);
			query.setQueryString(queryString);

		} catch (ConnectionException e) {
			if (e instanceof InvalidSObjectFault) {
				InvalidSObjectFault fault = (InvalidSObjectFault) e;
				throw new BadRequestException(fault.getExceptionCode().name().concat(": ").concat(fault.getExceptionMessage()));
			} else {
				throw new InternalServerErrorException(e.getMessage());
			}
		}
		
		return query;
	}
}
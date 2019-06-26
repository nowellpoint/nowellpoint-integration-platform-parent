package com.nowellpoint.listener;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.maps.errors.ApiException;
import com.nowellpoint.client.sforce.model.changeevent.ChangeEvent;
import com.nowellpoint.listener.model.AccountEvent;

public class AccountChangeEventHandler implements ChangeEventHandler {
	
	@Inject
    private Event<AccountEvent> changeEvent;
	
	@Override
	public void handleChangeEvent(ChangeEvent event, String organizationId, String refreshToken) throws ApiException, InterruptedException, IOException {
		
		AccountEvent accountEvent = AccountEvent.builder()
				.accountId(event.getPayload().getChangeEventHeader().getRecordIds().get(0))
				.payload(event.getPayload().getAttributes())
				.organizationId(organizationId)
				.refreshToken(refreshToken)
				.userId(event.getPayload().getChangeEventHeader().getCommitUser())
				.timestamp(event.getPayload().getChangeEventHeader().getCommitTimestamp())
				.transactionKey(event.getPayload().getChangeEventHeader().getTransactionKey())
				.changeType(event.getPayload().getChangeEventHeader().getChangeType())
				.build();
		
		changeEvent.fire(accountEvent);
	}
}
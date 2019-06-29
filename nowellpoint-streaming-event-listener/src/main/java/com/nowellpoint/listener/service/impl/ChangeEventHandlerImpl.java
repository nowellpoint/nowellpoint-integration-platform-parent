package com.nowellpoint.listener.service.impl;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.nowellpoint.listener.model.AccountEvent;
import com.nowellpoint.listener.model.ChangeEvent;
import com.nowellpoint.listener.service.AccountService;
import com.nowellpoint.listener.service.ChangeEventHandler;

public class ChangeEventHandlerImpl implements ChangeEventHandler {
	
	@Inject
	private AccountService accountService;
	
	@Override
	public void handleChangeEvent(@Observes ChangeEvent changeEvent) {
		
		if ("Account".equals(changeEvent.getPayload().getChangeEventHeader().getEntityName())) {
			
			AccountEvent accountEvent = AccountEvent.builder()
					.accountId(changeEvent.getPayload().getChangeEventHeader().getRecordIds().get(0))
					.payload(changeEvent.getPayload().getAttributes())
					.organizationId(changeEvent.getOrganizationId())
					.userId(changeEvent.getPayload().getChangeEventHeader().getCommitUser())
					.timestamp(changeEvent.getPayload().getChangeEventHeader().getCommitTimestamp())
					.transactionKey(changeEvent.getPayload().getChangeEventHeader().getTransactionKey())
					.changeType(changeEvent.getPayload().getChangeEventHeader().getChangeType())
					.build();
			
			accountService.processEvent(accountEvent);
		}
	}
}
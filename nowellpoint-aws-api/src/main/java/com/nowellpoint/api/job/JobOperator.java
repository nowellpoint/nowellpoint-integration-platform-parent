package com.nowellpoint.api.job;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.model.document.AccountProfile;
import com.nowellpoint.mongodb.DocumentManagerFactory;

@Singleton
@Startup
public class JobOperator {
	
	@Resource
	TimerService timerService;
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;
	
	@PostConstruct
	public void setTimer() {
		ScheduleExpression expression = new ScheduleExpression()
				.second("*/10")
				.minute("*")
				.hour("*");
		
		timerService.createCalendarTimer(expression);
		
		System.out.println("created timer");
		
		AccountProfile account = documentManagerFactory.createDocumentManager().fetch(AccountProfile.class, new ObjectId("5808408e392e00330aeef78d"));
		
		System.out.println(account.getName());
	}
	
	@Timeout 
	public void runTask() { System.out.println("Task concluded with success!"); }
}
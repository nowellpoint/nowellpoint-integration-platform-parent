package com.nowellpoint.console.model;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.service.ServiceClient;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonSerialize(as = Dashboard.class)
@JsonDeserialize(as = Dashboard.class)
public abstract class AbstractDashboard {
	
	public abstract List<UserLicense> getUserLicenses();
	
	@Value.Default
	public Date getLastRefreshedOn() {
		return Date.from(Instant.now());
	}
	
	@Value.Default
	public Integer getCustomObjectCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getApexClassCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getApexTriggerCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getRecordTypeCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getUserRoleCount() {
		return 0;
	}
	
	@Value.Default
	public Integer getProfileCount() {
		return 0;
	}
	
	public static Dashboard of(com.nowellpoint.console.entity.Dashboard entity) {
		return entity == null ? null : Dashboard.builder()
				.apexClassCount(entity.getApexClassCount())
				.apexTriggerCount(entity.getApexTriggerCount())
				.customObjectCount(entity.getCustomObjectCount())
				.lastRefreshedOn(entity.getLastRefreshedOn())
				.profileCount(entity.getProfileCount())
				.userLicenses(UserLicenses.of(entity.getUserLicenses()))
				.userRoleCount(entity.getUserRoleCount())
				.recordTypeCount(entity.getRecordTypeCount())
				.build();
	}
	
	public static Dashboard of(com.nowellpoint.client.sforce.model.Token token) throws InterruptedException, ExecutionException {
		
		ExecutorService executor = Executors.newFixedThreadPool(7);
		
		FutureTask<com.nowellpoint.client.sforce.model.DescribeGlobalResult> describeGlobalTask = new FutureTask<com.nowellpoint.client.sforce.model.DescribeGlobalResult>(
				new Callable<com.nowellpoint.client.sforce.model.DescribeGlobalResult>() {
					@Override
					public com.nowellpoint.client.sforce.model.DescribeGlobalResult call() {
						return ServiceClient.getInstance()
								.salesforce()
				 				.describeGlobal(token);
				   }
				}
		);
		
		executor.execute(describeGlobalTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.UserLicense>> getUserLicensesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.UserLicense>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.UserLicense>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.UserLicense> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getUserLicenses(token);
				   }
				}
		);
		
		executor.execute(getUserLicensesTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.ApexClass>> getClassesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.ApexClass>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.ApexClass>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.ApexClass> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getApexClasses(token);
				   }
				}
		);
		
		executor.execute(getClassesTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.ApexTrigger>> getTriggersTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.ApexTrigger>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.ApexTrigger>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.ApexTrigger> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getApexTriggers(token);
				   }
				}
		);
		
		executor.execute(getTriggersTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.RecordType>> getRecordTypesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.RecordType>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.RecordType>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.RecordType> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getRecordTypes(token);
				   }
				}
		);
		
		executor.execute(getRecordTypesTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.UserRole>> getUserRolesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.UserRole>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.UserRole>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.UserRole> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getUserRoles(token);
				   }
				}
		);
		
		executor.execute(getUserRolesTask);
		
		FutureTask<Set<com.nowellpoint.client.sforce.model.Profile>> getProfilesTask = new FutureTask<Set<com.nowellpoint.client.sforce.model.Profile>>(
				new Callable<Set<com.nowellpoint.client.sforce.model.Profile>>() {
					@Override
					public Set<com.nowellpoint.client.sforce.model.Profile> call() {
						return ServiceClient.getInstance()
								.salesforce()
								.getProfiles(token);
				   }
				}
		);
		
		executor.execute(getProfilesTask);
		
		AtomicInteger customObjectCount = new AtomicInteger(0);
		
		describeGlobalTask.get().getSObjects().stream().forEach(sobject -> {
			if (sobject.getCustom()) {
				customObjectCount.getAndIncrement();
			}
		});
		
		return Dashboard.builder()
				.apexClassCount(getClassesTask.get().size())
				.apexTriggerCount(getTriggersTask.get().size())
				.customObjectCount(customObjectCount.get())
				.lastRefreshedOn(Date.from(Instant.now()))
				.profileCount(getProfilesTask.get().size())
				.recordTypeCount(getRecordTypesTask.get().size())
				.userLicenses(getUserLicensesTask.get().stream()
						.map(f -> {
							return UserLicense.of(f);
						})
						.collect(Collectors.toList()))
				.userRoleCount(getUserRolesTask.get().size())
				.build();
	}
}
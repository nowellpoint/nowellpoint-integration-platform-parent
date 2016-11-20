package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.domain.Plan;

/**
 * 
 * 
 * @author jherson
 *
 *
 */

public class PlanModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.Plan> {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public PlanModelMapper() {
		super(com.nowellpoint.api.model.document.Plan.class);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	protected Plan findPlan(String id) {
		com.nowellpoint.api.model.document.Plan document = findById(id.toString());
		Plan plan = modelMapper.map(document, Plan.class);
		return plan;
	}	
	
	/**
	 * 
	 * 
	 * @param plan
	 * 
	 * 
	 */
	
	protected void createPlan(Plan plan) {
		com.nowellpoint.api.model.document.Plan document = modelMapper.map(plan, com.nowellpoint.api.model.document.Plan.class);
		create(document);
		modelMapper.map(document, plan);
	}
	
	/**
	 * 
	 * 
	 * @param plan
	 * 
	 * 
	 */
	
	protected void updatePlan(Plan plan) {
		com.nowellpoint.api.model.document.Plan document = modelMapper.map(plan, com.nowellpoint.api.model.document.Plan.class);
		replace(document);
		modelMapper.map(document, plan);
	}
	
	/**
	 * 
	 * 
	 * @param planCode
	 * @return
	 * 
	 * 
	 */
	
	protected Plan findByPlanCode(String planCode) {
		com.nowellpoint.api.model.document.Plan document = super.findOne( eq ( "planCode", planCode ) );
		Plan plan = modelMapper.map( document, Plan.class );
		return plan;
	}
	
	/**
	 * 
	 * 
	 * @param localeSidKey
	 * @param languageLocaleKey
	 * @return
	 * 
	 * 
	 */
	
	protected Set<Plan> getAllActive(String localeSidKey, String languageLocaleKey) {
		Set<com.nowellpoint.api.model.document.Plan> documents = super.find( and ( 
							eq ( "isActive", Boolean.TRUE ), 
							eq ( "localeSidKey", localeSidKey ), 
							eq ( "languageLocaleKey", languageLocaleKey ) ) );
		
		Set<Plan> plans = null;
		
		if (! documents.isEmpty()) {
			plans = modelMapper.map(documents, new TypeToken<HashSet<Plan>>() {}.getType());
		}
		
		return plans;
	}
	
	/**
	 * 
	 * 
	 * @param plan
	 * 
	 * 
	 */
	
	protected void deletePlan(Plan plan) {
		com.nowellpoint.api.model.document.Plan document = modelMapper.map(plan, com.nowellpoint.api.model.document.Plan.class);
		delete(document);
	}
}
package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.Plan;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class PlanCodec extends AbstractCodec<Plan> {
	
	public PlanCodec() {
		super(Plan.class);
	}
}
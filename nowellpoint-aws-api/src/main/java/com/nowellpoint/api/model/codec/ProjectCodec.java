package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.Project;
import com.nowellpoint.mongodb.document.AbstractCodec;

public class ProjectCodec extends AbstractCodec<Project> {

	public ProjectCodec() {
		super(Project.class);
	}
}
package com.nowellpoint.aws.api.codec;

import com.nowellpoint.aws.api.model.Project;
import com.nowellpoint.aws.data.AbstractCodec;

public class ProjectCodec extends AbstractCodec<Project> {

	public ProjectCodec() {
		super(Project.class);
	}
}
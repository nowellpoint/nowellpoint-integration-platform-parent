package com.nowellpoint.api.document.codec;

import com.nowellpoint.api.model.Project;
import com.nowellpoint.aws.data.AbstractCodec;

public class ProjectCodec extends AbstractCodec<Project> {

	public ProjectCodec() {
		super(Project.class);
	}
}
package com.nowellpoint.api.model.codec;

import com.nowellpoint.api.model.document.ProjectDocument;
import com.nowellpoint.aws.data.AbstractCodec;

public class ProjectCodec extends AbstractCodec<ProjectDocument> {

	public ProjectCodec() {
		super(ProjectDocument.class);
	}
}
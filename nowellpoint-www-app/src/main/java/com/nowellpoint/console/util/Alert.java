package com.nowellpoint.console.util;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;
import static j2html.TagCreator.strong;

import j2html.tags.UnescapedText;

public class Alert {
	
	public static String showError(String message) {
		return div().withId("error").withClass("alert alert-danger")
				.with(a().withClass("close").withData("dismiss", "alert")
						.with(new UnescapedText("&times;")))
				.with(div().with(strong().withText(message)))
				.render();
	}
}
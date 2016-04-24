package com.nowellpoint.www.app.util;

import static j2html.TagCreator.a;
import static j2html.TagCreator.div;

public class HtmlWriter {

	public static String success(String message) {
		return div()
				.attr("id", "success")
				.attr("class", "alert alert-success")
				.with(a().attr("class", "close").attr("data-dismiss","alert").withText("x"))
				.with(div().attr("class", "text-center").withText(message))
				.toString();
	}
	
	public static String error(String message) {
		return div()
				.attr("id", "error")
				.attr("class", "alert alert-success")
				.with(a().attr("class", "close").attr("data-dismiss","alert").withText("x"))
				.with(div().attr("class", "text-center").withText(message))
				.toString();
	}
}
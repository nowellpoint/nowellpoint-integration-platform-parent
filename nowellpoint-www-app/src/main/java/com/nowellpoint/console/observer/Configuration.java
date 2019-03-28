package com.nowellpoint.console.observer;

import javax.enterprise.event.Observes;

import com.nowellpoint.console.model.Organization;

public class Configuration {

	public void save(@Observes Organization event) {
		System.out.println("event observer: " + event.getName());
	}
}
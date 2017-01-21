package com.nowellpoint.mongodb.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;

@Qualifier
@ApplicationScoped
@Stereotype
@Target({ FIELD })
@Retention(RUNTIME)
public @interface Datasource {

}
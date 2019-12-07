package com.olbius.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Metric {
	String name();

	int estimationSize();

	long estimationTime();
	
	double smoothing();
	
	double threshold() default 0.0;
}

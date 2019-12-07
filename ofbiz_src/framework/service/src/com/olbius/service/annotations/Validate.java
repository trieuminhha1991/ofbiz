package com.olbius.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Validate {
	
	Class<?> classValidate() default org.ofbiz.base.util.UtilValidate.class;
	
	String method();
	
	String failMessage() default "";
	
	Property[] failProperty() default {};
}

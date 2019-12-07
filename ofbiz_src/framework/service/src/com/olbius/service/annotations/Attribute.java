package com.olbius.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.olbius.service.annotations.type.HtmlType;
import com.olbius.service.annotations.type.ModeType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Attribute {

	String name();
	
	Class<?> type();
	
	ModeType mode();
	
	boolean optional() default false;

	HtmlType allowHtml() default HtmlType.NONE;

	boolean formDisplay() default true;

	String defaultValue() default "";

	String formLabel() default "";

	String entity() default "";

	String fieldName() default "";

	String requestAttributeName() default "";

	String sessionAttributeName() default "";

	String stringMapPrefix() default "";

	String stringListSuffix() default "";
	
	String description() default "";
	
	Validate[] validate() default {};
}

package com.olbius.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.olbius.service.annotations.type.BooleanType;
import com.olbius.service.annotations.type.HtmlType;
import com.olbius.service.annotations.type.ModeType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OverrideAttribute {

	BooleanType optional() default BooleanType.NULL;

	HtmlType allowHtml() default HtmlType.NULL;

	BooleanType formDisplay()  default BooleanType.NULL;

	ModeType mode() default ModeType.NULL;

	String name();

	Class<?> type() default void.class;

	String defaultValue() default "";

	String formLabel() default "";

	String entity() default "";

	String fieldName() default "";
	
	Validate[] validate() default {};
}

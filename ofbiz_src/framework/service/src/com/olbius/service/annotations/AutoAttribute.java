package com.olbius.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.olbius.service.annotations.type.HtmlType;
import com.olbius.service.annotations.type.IncludeType;
import com.olbius.service.annotations.type.ModeType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoAttribute {
	boolean optional() default false;

	HtmlType allowHtml() default HtmlType.NONE;

	boolean formDisplay() default true;

	ModeType mode();

	String entity();

	IncludeType include() default IncludeType.ALL;
	
	String[] excludes() default {};
}

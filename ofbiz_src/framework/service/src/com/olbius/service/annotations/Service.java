package com.olbius.service.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.olbius.service.annotations.type.SemaphoreType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Service {

	String name();

	String engine() default "olbius-java";

	boolean auth() default false;

	boolean export() default false;

	boolean validate() default true;

	String defaultEntityName() default "";

	boolean useTransaction() default true;

	boolean requireNewTransaction() default false;

	boolean hideResultInLog() default false;

	int transactionTimeout() default 0;

	int maxRetry() default -1;

	boolean debug() default false;

	SemaphoreType semaphore() default SemaphoreType.NONE;

	int semaphoreWaitSeconds() default 300;

	int semaphoreSleep() default 500;

	String permission() default "";

	String loader() default "main";
}

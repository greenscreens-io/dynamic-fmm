/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify foreign method argument as a callback.
 * Callback can be MemoryAddress, or @Callback annotated Interface.
 * 
 * If @Callback is unnamed, first annotated method will be used.
 * It is considered that Interface contain only one method.
 * 
 * If @Callback is named, it is considered that Interface contains
 * multiple @Callback annotated methods which names must match-    
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD })
public @interface Callback {
	String name() default "";
}
